package org.drx.evoleq.fx.flow

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.conditions.once
import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.dsl.*
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.flow.intercept
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.application.SimpleAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.dsl.deprecated.action
import org.drx.evoleq.fx.phase.AppFlowMessage
import org.drx.evoleq.fx.phase.FxApplicationPhase
import org.drx.evoleq.message.Message
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.drx.evoleq.time.WaitForProperty
import org.junit.Test
import kotlin.reflect.KClass

class FxApplicationFlowTest {
    @Test fun basics() = runBlocking {

        class CloseDialog

        class Data(val initialized: Boolean = false ,val message: AppFlowMessage<Data> = AppFlowMessage.Runtime.Wait<Data>())

        class App : SimpleAppManager<Data>() {
            override fun configure(): Stub<Data> = stub appStub@{
                // configure flow of this specific application
                id(App::class)
                evolve{ data -> when(val message = data.message){
                    is AppFlowMessage.Runtime -> when(message){
                        is AppFlowMessage.Runtime.EnteredRuntimePhase -> Parallel{
                            Data(data.initialized,AppFlowMessage.Runtime.ShowStage(Stage::class))
                        }
                        is AppFlowMessage.Runtime.Wait -> Parallel{
                            val nextMessage = receiverStub.evolve(message).get()
                            Data(data.initialized, nextMessage)
                        }
                        is AppFlowMessage.Runtime.ShowStage -> Parallel{
                            applicationManagerPort.send(AppFlowMessage.Runtime.ShowStage<Data>(message.id))
                            Data(data.initialized,AppFlowMessage.Runtime.Wait())
                        }
                        is AppFlowMessage.Runtime.HideStage -> when(message.id) {
                            Stage::class -> data.intercept(
                                appStub<Data>(App::class)!!.toFlow<Data,Boolean>(
                                    conditions{
                                        testObject(true)
                                        check{b->b}
                                        updateCondition { data -> data.message !is AppFlowMessage.Runtime.Confirm<*> }
                                    }
                                ),
                                gap{
                                    from{ data -> Parallel{
                                        Data(data.initialized, AppFlowMessage.Runtime.ShowStage(CloseDialog::class))
                                    }}
                                    to{ data, confirm -> when(confirm.message){
                                        is AppFlowMessage.Runtime.Confirm.Ok<*> -> Parallel{
                                            applicationManagerPort.send(AppFlowMessage.Runtime.HideStage<Data>(Stage::class))
                                            Data(true, AppFlowMessage.Runtime.Terminate())
                                        }
                                        is AppFlowMessage.Runtime.Confirm.Cancel<*> -> Parallel{

                                            Data(data.initialized,AppFlowMessage.DriveStub(stubs[Stage::class]!! as Stub<Data>))
                                        }
                                        else -> Parallel{Data(data.initialized, AppFlowMessage.Runtime.Wait())}
                                    }}
                                }
                            )
                            else -> Parallel{
                                applicationManagerPort.send(message)
                                Data(data.initialized,AppFlowMessage.Runtime.Wait())
                            }
                        }
                        is AppFlowMessage.Runtime.Terminate -> Parallel{
                            applicationManagerPort.send(message)
                            Data(data.initialized, AppFlowMessage.Runtime.Wait())
                        }
                        is AppFlowMessage.Runtime.Confirm -> Immediate {
                            Data(data.initialized, AppFlowMessage.Runtime.Wait())
                        }
                        /*
                        else -> Immediate{
                            Data(data.initialized,AppFlowMessage.Runtime.Wait())
                        }
                        */

                    }
                    is AppFlowMessage.FxComponentShown -> Parallel{
                        //delay(1_000)
                        stubs[message.component.id] = message.component
                        Data(data.initialized, AppFlowMessage.DriveStub(message.component as Stub<Data>))
                    }
                    is AppFlowMessage.DriveStub -> when(val id = message.stub.id){
                        Stage::class -> Parallel{
                            message.stub.toFlow<Data,Boolean>(conditions{
                                testObject(true)
                                check{b->b}
                                updateCondition { data -> data.message !is AppFlowMessage.Runtime.HideStage<Data> }
                            }).evolve(data).get()
                        }
                        CloseDialog::class -> Parallel{
                            val choice = (message.stub as Stub<AppFlowMessage.Runtime.Confirm<Data>>).toFlow<AppFlowMessage.Runtime.Confirm<Data>,Boolean>(
                                    conditions {
                                        testObject(true)
                                        check{b -> b}
                                        updateCondition { false }
                                    }
                            ).evolve(AppFlowMessage.Runtime.Confirm.Cancel()).get()
                            applicationManagerPort.send(AppFlowMessage.Runtime.HideStage<Data>(CloseDialog::class))
                            Data(data.initialized, choice)
                        }
                        else -> Immediate{Data(true, AppFlowMessage.Runtime.Wait())}
                    }
                    else -> Immediate{Data(data.initialized, AppFlowMessage.Runtime.Wait())}
                } }
            }
        }

        fun mainStage() = fxStage<Data> {
            id<Stage>()
            properties{
                "close" to SimpleObjectProperty<Boolean>(false)
            }
            view{configure{
                onCloseRequest {
                    consume()
                    (property("close") as SimpleObjectProperty<Boolean>).value = true
                }
            }}
            scene(fxScene{
                id<Scene>()
                view{configure{}}
                root(fxButton {
                    noStub()
                    view{configure{
                        text = "Hello"
                    }}
                }){ root -> Scene(root, 200.0,300.0) }

                stub(stub{})
            })
            stub(stub{
                evolve{ Parallel{
                    val b = WaitForProperty(property("close") as SimpleObjectProperty<Boolean>).toChange().get()
                    (property("close") as SimpleObjectProperty<Boolean>).value = false
                    Data(it.initialized, AppFlowMessage.Runtime.HideStage(Stage::class))}
                }
            })
        }

        fun closeDialog() = fxStage<AppFlowMessage.Runtime.Confirm<Data>>{
            id<CloseDialog>()
            val clicked = SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>()
            //properties{"confirm" to SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>() }
            view{configure{}}

            scene(fxScene{
                id<Scene>()
                view{configure{}}
                root(fxVBox{
                    //id<Button>()
                    noStub()
                    view{configure{}}
                    child(fxButton{
                        noStub()
                        view{configure{
                            text = "Cancel"
                            action{
                                clicked.value = AppFlowMessage.Runtime.Confirm.Cancel()
                                //(property("confirm") as SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>).value = AppFlowMessage.Runtime.Confirm.Ok()
                            }
                        }}
                    })
                    child(fxButton{
                        noStub()
                        view{configure{
                            text = "Ok"
                            action{
                                clicked.value = AppFlowMessage.Runtime.Confirm.Ok()
                                //(property("confirm") as SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>).value = AppFlowMessage.Runtime.Confirm.Ok()
                            }
                        }}
                    })
                }){
                    root-> Scene(root, 300.0, 200.0)
                }
                stub(stub{})
            })

            stub(stub{
                evolve{message -> when(message){
                    is AppFlowMessage.Runtime.Confirm -> Parallel{
                        val choice = WaitForProperty(
                                clicked
                                //property("confirm") as SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>
                        ).toChange().get()
                        choice
                    }
                    else -> Immediate{AppFlowMessage.Runtime.Confirm.Cancel<Data>()}
                }}
            })
        }

        val components = fxComponents {
            stage(Stage::class){mainStage()}
            stage(CloseDialog::class){closeDialog()}
        }

        val port: BaseReceiver<AppFlowMessage<Data>> = receiver{}
        val portObserver = receivingStub<AppFlowMessage<Data>,AppFlowMessage<Data>> {
            evolve { Parallel{it} }
            gap{
                from{ Immediate{it} }
                to{ _, response -> Immediate{
                    response
                } }
            }
            receiver(port)
        }

        open class MainFlow
        class Launch : MainFlow()
        class Wait : MainFlow()
        class Teminate : MainFlow()

        val mainStub = stub<Data> mainStub@{
            evolve{data -> Parallel{
                val phase = Parallel<FxApplicationPhase<Data>>{
                    val terminate = fxApplicationManagerFlow<Data, KClass<App>>().evolve(
                        FxApplicationPhase.Launch<Data>(
                            App::class,
                            components,
                            port
                        )
                    ).get()
                    terminate
                }
                val d = Parallel<Data> {
                    val message = portObserver.evolve(AppFlowMessage.Runtime.Wait()).get()
                    //println("hu $message")
                    when(message){
                        is AppFlowMessage.AppStubLaunched<*> -> {
                            val stub = message.stub as Stub<Data>
                            this@mainStub.stubs[stub.id] = stub
                            stub.toFlow<Data, Boolean>(
                                    conditions{
                                        testObject(true)
                                        check{b->b}
                                        updateCondition { data ->  !data.initialized}
                                    }
                            ).evolve(data).get()
                        }
                        else -> data
                    }
                }.get()
                //assert(phase.get() == FxApplicationPhase.Terminate<Data>())
                d
            }}
        }

        val mainFlow = mainStub.toFlow<Data, Boolean>(
            conditions{
                testObject(true)
                check{b->b}
                updateCondition { data -> !data.initialized  }
            }
        )

        val res = mainFlow.evolve(Data()).get()
        port.actor.close()
        delay(1_000)
        Unit
    }


}