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
import kotlinx.coroutines.*
import org.drx.evoleq.coroutines.onScope
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.launchStage
import org.drx.evoleq.fx.util.showStage
import org.junit.Test

class FxStageTest{


    @Test fun fxStage() = fxRunTest{//runBlocking {
        val stub = DEFAULT_FX_COMPONENT_SCOPE().parallel{
            showStage( testStageConfig() ).get()
            //delay(2_000)
        }.get()

        //delay (1_000)
    }

    private fun testStageConfig() = onScope{scope: CoroutineScope->fxStage<Nothing>(scope){
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
                        assert(true)
                    }}
                })
            }) {
                root -> Scene(root, 200.0,200.0)
            }
            stub(stub{})
        })

        stub(stub{})
    }}

    //@Test
    fun configuration() = fxRunTest{

        var done = false
        var stageDone = false
        val scope = DEFAULT_FX_COMPONENT_SCOPE()
        var stage: Stage? = null
        scope.parallel{
            stage = fxStage<Unit>(this) {
                id<Stage>()
                view{configure{
                    x = 300.0
                    y = 400.0
                    width = 200.0
                    height = 300.0
                    title = "jfdklajfdk"
                }}
                scene(fxScene{
                    noStub()
                    view{configure{}}
                    root(fxPane{
                        noStub()
                        view{configure{}}

                    }){ root -> Scene(root) }
                    fxRunTime { done = true }
                })
                stub(stub{})
                fxRunTime{stageDone = true}
            }.show()
        }
        withTimeout(30000) {
            while(!done || !stageDone) {
                delay(1)
            }
        }
        stage!!.showAndWait()
        delay(40000)

    }
}