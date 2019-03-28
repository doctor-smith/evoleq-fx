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

import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.test.launchTestStage
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxStageTest{

    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun fxStage() = runBlocking {
        val stub = launchTestStage(testStageConfig()).get()

        delay (1_000)
    }

    fun testStageConfig(): FxComponent<Stage, Nothing> = fxStage{
        //id<StageId>()
        view{configure{}}
        scene(fxScene{
            root(fxBorderPane{
                view {configure{}}
                top(fxButton {
                    view{configure{text = "Hi"}}
                    noStub()
                })
                center(fxAnchorPane {
                    view{configure{ style = "-fx-background-color: red;"}}
                    child(fxButton {
                        view{configure{
                            text = "center"
                            leftAnchor(10.0)
                            topAnchor(20.0)
                        }}
                        noStub()
                    })
                    noStub()
                })
                bottom(fxButton {
                    view{configure{
                        text = "Bottom"
                    }}
                    noStub()
                })
                noStub()
            }) {
                root -> Scene(root, 200.0,200.0)
            }
            noStub()
        })
        noStub()
        //stub(stub{})
    }
}