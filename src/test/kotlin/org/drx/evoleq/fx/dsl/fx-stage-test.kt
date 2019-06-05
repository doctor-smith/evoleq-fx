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
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.launchStage
import org.junit.Test

class FxStageTest{


    @Test fun fxStage() = fxRunTest{//runBlocking {
        val stub = launchStage(testStageConfig()).get()

        //delay (1_000)
    }

    fun testStageConfig(): FxComponent<Stage, Nothing> = fxStage{
        id<StageId>()
        //noStub()
        view{configure{}}
        scene(fxScene{
            id<Scene>()
            root(fxBorderPane{
                noStub()
                view {configure{}}
                top(fxButton {
                    noStub()
                    view{configure{text = "Hi"}}
                })
                center(fxAnchorPane {
                    noStub()
                    view{configure{ style = "-fx-background-color: red;"}}
                    child(fxButton {
                        noStub()
                        view{configure{
                            text = "center"
                            leftAnchor(10.0)
                            topAnchor(20.0)
                        }}
                    })
                })
                bottom(fxButton {
                    noStub()
                    view{configure{
                        text = "Bottom"
                    }}
                })
            }) {
                root -> Scene(root, 200.0,200.0)
            }
            stub(stub{})
        })

        stub(stub{})
    }
}