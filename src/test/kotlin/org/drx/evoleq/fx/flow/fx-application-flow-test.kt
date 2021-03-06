/**
 * Copyright (c) 2019 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drx.evoleq.fx.flow

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.stage.WindowEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.coroutines.onScope
import org.drx.evoleq.coroutines.onScopeSuspended
import org.drx.evoleq.dsl.*
import org.drx.evoleq.dsl.gap
import org.drx.evoleq.dsl.receiver
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.flow.intercept
import org.drx.evoleq.fx.application.configration.SimpleAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.phase.AppFlowMessage
import org.drx.evoleq.fx.phase.FxApplicationPhase
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.drx.evoleq.time.WaitForProperty
import org.testfx.api.FxRobot
import kotlin.reflect.KClass

class FxApplicationFlowTest {
   /* todo flow does not terminate for some reason: -> interplay of receiverStub and its observer?  */
    //@Test
    fun basicsMulti() = fxRunTest(60_000){

        class CloseDialog

        open class MainFlow
        class Launch : MainFlow()
        class Wait : MainFlow()
        class Teminate : MainFlow()

        class Data(val initialized: Boolean = false ,val message: AppFlowMessage<Data> = AppFlowMessage.Runtime.Wait<Data>(), val mainFlowMessage: MainFlow = Launch())
        var stage : Stage? = null
        var okButton : Button? = null
        var cancelButton : Button? = null
        class App : SimpleAppManager<Data>() {
            override fun configure(): Stub<Data> = scope.stub appStub@{
                // configure flow of this specific application
                id(App::class)
                evolve{ data -> when(val message = data.message){
                    is AppFlowMessage.Runtime -> when(message){
                        is AppFlowMessage.Runtime.EnteredRuntimePhase -> scope.parallel{
                            Data(data.initialized,AppFlowMessage.Runtime.ShowStage(Stage::class))
                        }
                        is AppFlowMessage.Runtime.Wait -> scope.parallel{
                            val nextMessage = receiverStub.evolve(message).get()
                            Data(data.initialized, nextMessage)
                        }
                        is AppFlowMessage.Runtime.ShowStage -> scope.parallel{
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
                                    from{ data -> scope.parallel{
                                        Data(data.initialized, AppFlowMessage.Runtime.ShowStage(CloseDialog::class))
                                    }}
                                    to{ data, confirm -> when(confirm.message){
                                        is AppFlowMessage.Runtime.Confirm.Ok<*> -> scope.parallel{
                                            applicationManagerPort.send(AppFlowMessage.Runtime.HideStage<Data>(Stage::class))
                                            Data(false, AppFlowMessage.Runtime.Terminate())
                                        }
                                        is AppFlowMessage.Runtime.Confirm.Cancel<*> -> scope.parallel{

                                            Data(data.initialized,AppFlowMessage.DriveStub(stubs[Stage::class]!! as Stub<Data>))
                                        }
                                        else -> scope.parallel{Data(data.initialized, AppFlowMessage.Runtime.Wait())}
                                    }}
                                }
                            )
                            else -> scope.parallel{
                                applicationManagerPort.send(message)
                                Data(data.initialized,AppFlowMessage.Runtime.Wait())
                            }
                        }
                        is AppFlowMessage.Runtime.Terminate -> scope.parallel{
                            println("TERMINATE")
                            applicationManagerPort.send(message)
                            Data(data.initialized, AppFlowMessage.Runtime.Wait())
                        }
                        is AppFlowMessage.Runtime.Confirm -> scope.parallel {
                            Data(data.initialized, AppFlowMessage.Runtime.Wait())
                        }
                        /*
                        else -> Immediate{
                            Data(data.initialized,AppFlowMessage.Runtime.Wait())
                        }
                        */

                    }
                    is AppFlowMessage.FxComponentShown -> scope.parallel{
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
                        CloseDialog::class -> scope.parallel{
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
                        else -> scope.immediate{Data(true, AppFlowMessage.Runtime.Wait())}
                    }
                    is AppFlowMessage.Terminated -> scope.immediate{
                        println("TERMINATED")
                        Data(true,message)
                    }
                    else -> scope.immediate{Data(data.initialized, AppFlowMessage.Runtime.Wait())}
                } }
            }
        }

        val mainStage = onScopeSuspended{scope: CoroutineScope ->fxStage<Data>(scope) {
            id<Stage>()
            properties{
                "close" to SimpleObjectProperty<Boolean>(false)
            }
            view{configure{
                onCloseRequest {
                    consume()
                    (property("close") as SimpleObjectProperty<Boolean>).value = true
                }
                x = 200.0
                y = 200.0
                stage = this
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
                evolve{ parallel{
                    val b = WaitForProperty(property("close") as SimpleObjectProperty<Boolean>).toChange().get()
                    (property("close") as SimpleObjectProperty<Boolean>).value = false
                    Data(it.initialized, AppFlowMessage.Runtime.HideStage(Stage::class))}
                }
            })

        }}

        val  closeDialog =onScopeSuspended{scope: CoroutineScope -> fxStage<AppFlowMessage.Runtime.Confirm<Data>>(scope){
            id<CloseDialog>()
            val clicked = SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>()
            //properties{"confirm" to SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>() }
            view{configure{
                x = 200.0
                y = 200.0
            }}

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
                            id = "calncel"
                            cancelButton = this
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
                            id="ok"
                            okButton = this
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
                    is AppFlowMessage.Runtime.Confirm -> parallel{
                        val choice = WaitForProperty(
                                clicked
                                //property("confirm") as SimpleObjectProperty<AppFlowMessage.Runtime.Confirm<Data>>
                        ).toChange().get()
                        choice
                    }
                    else -> Immediate{AppFlowMessage.Runtime.Confirm.Cancel<Data>()}
                }}
            })
        }}

        val components = fxComponents {
            stage(Stage::class, mainStage)
            stage(CloseDialog::class,closeDialog)
        }

        val port: BaseReceiver<AppFlowMessage<Data>> = GlobalScope.receiver{}
        val portObserver = GlobalScope.receivingStub<AppFlowMessage<Data>,AppFlowMessage<Data>> {
            evolve { parallel{it} }
            gap{
                from{ immediate{it} }
                to{ _, response -> immediate{
                    //println("portObserver@testClass")
                    println("\n>>>>>>>>>>>>>>>>>>>>>> $response \n")
                    response
                } }
            }
            receiver(port)
        }



        /* TODO make this a real stub */
        val mainStub = stub<Data> mainStub@{
            evolve{data -> parallel{
                val phase = parallel<FxApplicationPhase<Data>>{
                    val terminate = fxApplicationManagerFlow<Data, KClass<App>>().evolve(
                        FxApplicationPhase.Launch<Data>(
                            App::class,
                            components,
                            port
                        )
                    ).get()
                    terminate
                }
                val d = parallel<Data> {
                    val message = portObserver.evolve(AppFlowMessage.Runtime.Wait()).get()
                    //println("hu $message")
                    when(message) {
                        is AppFlowMessage.AppStubLaunched<*> -> parallel {
                            val stub = message.stub as Stub<Data>
                            this@mainStub.stubs[stub.id] = stub
                            stub.toFlow<Data, Boolean>(
                                    conditions {
                                        testObject(true)
                                        check { b -> b }
                                        updateCondition { data -> !data.initialized }
                                    }
                            ).evolve(data).get()
                        }.get()
                        is AppFlowMessage.Terminated -> parallel {
                            println(message)
                            Data(true, message)
                        }.get()

                        else -> parallel{data}.get()
                    }
                }.get()
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


        //
        val res = parallel<Data>{mainFlow.evolve(Data()).get()}

        val click = parallel<Unit>{
            while(stage == null){
                kotlinx.coroutines.delay(1)
            }
            delay(1000)
            parallelFx<Unit> {
                stage!!.fireEvent(WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                ))
            }
            while(okButton == null) {
                kotlinx.coroutines.delay(1)
            }
            parallel<Unit>{
                FxRobot().clickOn(okButton!!)
            }.get()
        }
        //res.get()
        click.get()

        //port.actor.close()
        //delay(1_000)
        //cancel()
        println("'End")
        Unit
    //}.get()()
    }


}