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
import javafx.scene.control.TabPane
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.coroutines.onScope
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.showStage
import org.drx.evoleq.stub.*
import org.junit.Test

class TabPaneTest {

    /* TODO tab-component*/
    //@Test
    fun basics() = fxRunTest{//runBlocking {
        var done = false

        val stageComponent = onScope{scope: CoroutineScope ->fxStage<Nothing>(scope){
            id<StageId>()
            view{configure{}}
            scene(fxScene {
                id<SceneId>()
                root(fxTabPane {
                    noStub()
                    view{configure{}}
                    tab(0,fxTab<Nothing> {
                        noStub()
                        view{configure{

                        }}
                        content(fxButton<Nothing> {
                            noStub()
                            view{configure{
                                text = "BUTTON"
                            }.style("""-fx-color: crimson;""")}

                        })

                    })
                    tab(1,fxTab<Nothing>{
                        noStub()
                        view{configure{}}
                        content(fxButton<Nothing> {
                            noStub()
                            view{configure{
                                text = "BUTTON1"
                                done = true
                            }.style("""-fx-color: crimson;""")}

                        })

                    })

                }){
                    root -> Scene(root, 300.0, 400.0)
                }
                stub(stub{})
            })
            stub(stub{})
        }}

        val stub = showStage(stageComponent).get()
        assert(done)
       // delay(1_000)
    }
}