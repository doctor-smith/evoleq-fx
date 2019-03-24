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

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.flow.Config.style1
import org.drx.evoleq.fx.test.showTestStage
import org.drx.evoleq.stub.*
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxComponentFlowTest {
    @Before fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }


    @Test fun example() = runBlocking {
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
                        stub(org.drx.evoleq.dsl.stub {
                            evolve{ x -> Immediate{x+1}
                            }
                        })
                    })
                    child(fxBorderPane<Nothing> {
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
                                view{ configure{
                                    text = "TOP"
                                    action{
                                        this@stage.shutdown()
                                        Platform.exit()
                                    }
                                }}
                                tooltip(fxTooltip{
                                    view{configure{
                                        text = "Da werden Sie geholfen!"
                                    }}
                                    noStub()
                                })
                                tunnel()
                            })
                            tunnel()
                        })
                        bottom( fxButton {
                            id<BottomId>()
                            view{configure{ text = "BOTTOM" }}

                            noStub()
                        })
                        tunnel()
                    })
                }){
                    root -> Scene(root, 300.0, 200.0)
                }
                tunnel()
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

}