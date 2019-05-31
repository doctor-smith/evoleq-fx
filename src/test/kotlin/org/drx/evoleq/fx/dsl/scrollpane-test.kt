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
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.dsl.deprecated.StageStubKey
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.showStage
import org.drx.evoleq.stub.Key2
import org.junit.Test

class ScrollPaneTest {


    @Test fun basics() = fxRunTest{//runBlocking {
        val stageComponent = fxComponent<Stage, Nothing> {
            id(StageStubKey::class)
            view{configure{title = "ScrollPane"}}
            scene(fxScene {
                id(SceneId::class)
                root(fxScrollPane {
                    //id(Key1::class)
                    noStub()
                    view{ configure{

                    } }
                    content(fxComponent<VBox, Nothing> {
                        noStub()
                        view{
                            configure{
                                IntRange(1,20).forEach {
                                    val button = Button("$it")
                                    button.minWidth = 300.0
                                    children.add(button)
                                }
                            }
                        }
                    })

                }){
                    root -> Scene(root,150.0,300.0)
                }
                stub(stub{})
            })
            stub(stub{})
        }
        showStage(stageComponent).get()

        //delay(1_000)
    }
}