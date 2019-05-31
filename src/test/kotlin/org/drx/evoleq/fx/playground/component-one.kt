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
package org.drx.evoleq.fx.playground

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.effect.Reflection
import javafx.scene.layout.AnchorPane
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.showStage
import org.junit.Test

class ComponentOneTest {
    /*
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
*/
    @Test fun go() = fxRunTest{//runBlocking {
        val stageComponent = {fxStage<Nothing> {
            id<StageId>()
            view { configure {
                //isResizable = false
            } }
            scene(fxScene {
                tunnel()
                root(fxBorderPane {
                    tunnel()
                    view { configure {} }

                    center(fxText<Nothing> {
                            noStub()
                            view {
                                configure {
                                    text = "EvoleqFx"
                                    effect = Reflection()
                                }.style("""
                                -fx-font-size: 70;
                            """.trimIndent())
                            }

                    })

                    bottom(fxAnchorPane {
                        id<AnchorPane>()
                        stub(stub{})
                        view{configure {  }}

                        child(fxButton{
                            id<Button>()
                            view{configure{
                                text = "Ok"

                                fxRunTime {
                                    bottomAnchor(10.0)
                                    rightAnchor((10.0))
                                }


                            }.style("""
                                -fx-color: crimson;
                            """.trimIndent()
                            )
                            }
                            stub(stub{})
                        })
                   })
                }){
                    root -> Scene(root, 350.0,230.0)
                }


            })
            stub(stub{})
        }}

        val stub = showStage(stageComponent()).get()

        delay(1_000)
    }
}