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
package org.drx.evoleq.fx.dsl

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.dsl.deprecated.SceneStubKey
import org.drx.evoleq.fx.dsl.deprecated.StageStubKey
import org.drx.evoleq.fx.test.fxRunTest
import org.drx.evoleq.fx.test.showTestStage
import org.drx.evoleq.stub.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class TabPaneTest {
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

    @Test fun basics() = fxRunTest{//runBlocking {
        var done = false

        val stageComponent = fxStage<Nothing>{
            id<StageId>()
            view{configure{}}
            scene(fxScene {
                id<SceneId>()
                root(fxTabPane {
                    id<Key1>()
                    view{configure{}}
                    tab(fxTab {
                        id<Key2>()
                        view{configure{

                        }}
                        content(fxButton<Nothing> {
                            id<Key3>()
                            view{configure{
                                text = "BUTTON"
                            }.style("""-fx-color: crimson;""")}
                            noStub()
                        })
                        noStub()
                    })
                    tab(fxTab{
                        id<Key4>()
                        view{configure{}}
                        content(fxButton<Nothing> {
                            id<Key5>()
                            view{configure{
                                text = "BUTTON1"
                                done = true
                            }.style("""-fx-color: crimson;""")}
                            noStub()
                        })
                        noStub()
                    })
                    noStub()
                }){
                    root -> Scene(root, 300.0, 400.0)
                }
                noStub()
            })
            noStub()
        }

        val stub = showTestStage(stageComponent).get()
        assert(done)
       // delay(1_000)
    }
}