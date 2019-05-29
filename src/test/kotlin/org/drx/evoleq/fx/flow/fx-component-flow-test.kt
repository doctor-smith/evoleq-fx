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

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.text.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.flow.Config.style1
import org.drx.evoleq.fx.test.fxRunTest
import org.drx.evoleq.fx.test.showTestStage
import org.drx.evoleq.stub.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxComponentFlowTest {
    var m : Application? = null
    @Before
    fun launchBgAppManager() = fxRunTest{//runBlocking {
        FxToolkit.registerPrimaryStage()
        m = FxToolkit.setupApplication { BgAppManager() }
    }
    @After
    fun cleanUp() = fxRunTest{// {
        FxToolkit.cleanupApplication(m!!)
        FxToolkit.cleanupStages()
    }


    @Test fun example() = fxRunTest{//runBlocking {
        val stageConfiguration = fxStage<Nothing> stage@{
            val closeButtonClicked = SimpleBooleanProperty(false)
            id<StageId>()
            view{
                configure {
                    //initStyle(StageStyle.UNDECORATED)
                    title = "title"
                    onCloseRequest {
                        consume()
                        closeButtonClicked.value = true
                        shutdown()
                    }
                }

            }
            scene(fxScene {
                //id<SceneId>()
                //stylesheet(style1)
                tunnel()
                root(fxAnchorPane{
                    //id<RootId>()
                    view{ configure{} }
                    tunnel()

                    child(fxButton<Int>{
                        id<Button>()
                        view{
                            configure{
                                text = "text"
                                leftAnchor(20)
                                bottomAnchor(10)
                            }.style(
                                    """-fx-color: green;"""
                            )
                        }
                        stub(stub {
                            evolve{ x -> Immediate{x+1}
                            }
                        })
                    })
                    child(fxBorderPane<Nothing> {
                        tunnel()
                        view{
                            configure{
                                leftAnchor( 10)
                                bottomAnchor(50.0)
                                topAnchor(10)
                            }
                        }

                        top(fxStackPane {
                            id<TopId>()
                            view{configure{}}
                            child<Button,Nothing>(fxButton {
                                //id<Key5>()
                                tunnel()
                                view{ configure{
                                    text = "TOP"
                                    action{
                                        this@stage.shutdown()
                                        Platform.exit()
                                    }
                                }}
                                tooltip(fxTooltip{
                                    noStub()
                                    view{configure{
                                        text = "Da werden Sie geholfen!"
                                    }}

                                })

                            })
                            stub(stub{})
                            //tunnel()
                        })
                        bottom( fxButton {
                            id<BottomId>()
                            view{configure{ text = "BOTTOM" }}
                            stub(stub{})
                            //noStub()
                        })

                    })
                }){
                    root -> Scene(root, 300.0, 200.0)
                }

            })

            fxRunTime {
                title = "FxRunTimeTitle"
            }

            stub(stub{})
            this@stage.stubAction {
                val s = findByKey(Button::class)!! as FxComponent<Button, Int>
                Parallel<Unit>{
                    val x = s.evolve(0).get()
                    println(">>>result = $x")
                }
                this.stubs[Button::class]!!
            }

        }

        val stub = showTestStage(stageConfiguration).get()
        //FxRobot().clickOn()
        delay(1_000)
    }

    //@Test
     fun exception() = runBlocking {
        val component = fxComponent<Node, Int> {  }
        //delay(5_000)

    }


    @Test fun tunnelsAndNoStubs() = fxRunTest{//runBlocking{
        val stageComponent = fxStage<Unit> {
            id<StageId>()
            view{configure{}}
            stub(stub{})

            scene(fxScene{
                tunnel()
                root(fxPane{
                    tunnel()
                    view{configure{}}

                    child(fxButton{
                        id<Button>()
                        view{configure{text = "Button"}}
                        stub(stub{})
                    })
                    child(fxButton{
                        noStub()
                        view{configure{}}
                    })
                    child(fxVBox<Nothing>{
                        tunnel()
                        view{configure{}}
                        child(fxLabel<Nothing> {
                            id<Label>()
                            view{configure{}}
                            stub(stub{})
                        })
                        child(fxVBox<Nothing>{
                            noStub()
                            view{configure{}}
                            // this child shall not appear as a sub-stub of the fx-stage-component
                            child(fxText<Nothing> {
                                id<Text>()
                                view{configure{}}
                                stub(stub{})
                            })
                        })
                    })
                })
            })

            stubAction{
                stubs[Button::class]!!
                stubs[Label::class]!!
            }
        }

        val stub = showTestStage(stageComponent).get()
        delay(1_000)
        //println(stageComponent.stubs.size)
        //stageComponent.stubs.forEach{println(it)}
        assert(stageComponent.stubs.size ==3) // 3 because the ApplicationManager is also a sub-stub

    }
}