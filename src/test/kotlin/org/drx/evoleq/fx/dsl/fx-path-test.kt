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

import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import org.drx.evoleq.coroutines.onScope
import org.drx.evoleq.coroutines.onScopeSuspended
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.fx.geometry.Derivation
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.showStage
import org.junit.Test
import java.nio.file.Path

class FxPathTest {

    @Test
    fun fxPath() = fxRunTest{//runBlocking {
        val stageComponent = onScopeSuspended{ scope: CoroutineScope ->fxStage<Unit>(scope) {
            id<StageId>()
            view{configure{}}
            scene(fxScene{
                tunnel()
                root(fxPane {
                    tunnel()
                    view{configure{}}
                    child(fxPath<Unit>{
                        id<Path>()
                        view{configure {}}
                        elements{
                            moveTo {
                                x = 10.0
                                y = 100.0
                            }

                            quadCurveTo {
                                x = 390.0
                                y = 100.0
                                endVelocity {
                                    x = 200
                                    y = 200
                                }

                            }
                            //closePath()
                        }
                        stub(stub{
                        })
                    })

                    child(fxPath<Unit>{
                        noStub()
                        view{configure{
                            stroke = Color.GREEN
                        }}
                        elements{
                            moveTo{
                                x = 0.0
                                y = 300.0
                            }
                            cubicCurveTo(
                                    startVelocity = Point2D(100.0, 100.0),
                                    endVelocity = Point2D(100.0, 100.0)
                            ){
                                x = 100.0
                                y = 300.0
                            }
                            cubicCurveTo(
                                    startVelocity = Point2D(100.0, 100.0),
                                    endVelocity = Point2D(100.0, 100.0)
                            ){
                                x = 200.0
                                y = 300.0
                            }
                            cubicCurveTo(
                                    startVelocity = Point2D(100.0, 100.0),
                                    endVelocity = Point2D(100.0, 100.0)
                            ){
                                x = 300.0
                                y = 300.0
                            }
                            cubicCurveTo(
                                    startVelocity = Point2D(100.0, 100.0),
                                    endVelocity = Point2D(100.0, 100.0)
                            ){
                                x = 400.0
                                y = 300.0
                            }
                        }
                    })

                    child(fxCubicCurve<Nothing> {

                        properties{
                            "start" to Derivation(1,100,0,0)
                            "end" to Derivation(100,111,0,0)
                        }

                        noStub()
                        view{configure{
                            stroke = Color.CRIMSON

                            start(property("start"))
                            end(property("end"))
                        }}

                    })
                }){
                    root -> Scene(root, 400.0,400.0)
                }
            })
            stub(stub{
                evolve{
                    Immediate{

                    }
                }
            })
        }}

        val stub = //GlobalScope.
         showStage(stageComponent).get()
        //delay(1_000)
    }
}