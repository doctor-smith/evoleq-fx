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

import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.scene.shape.StrokeLineJoin
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.showOnStage
import org.drx.evoleq.fx.util.showNodeOnStage
import org.junit.Test

class FxSvgPathTest{


    //@Test
    fun fxSvgPath() = fxRunTest{//runBlocking{
        val path = {fxSvgPath<Nothing>{
            id<SVGPath>()
            stub(stub{})
            view{configure {
                fill = Color.TRANSPARENT
                stroke = Color.CRIMSON
                strokeWidth = 10.0
                elements {
                    moveTo {
                        x = 10
                        y = 10
                    }
                    lineTo {
                        x = 200
                        y = 200
                    }
                    cubicCurveTo {
                        absolute = false
                        controlX2 = 50
                        controlY2 = 70
                        x = 100
                        y = -100
                    }
                    quadraticCurveTo {
                        x = 40
                        y = 10
                    }
                    closePath()
                    moveTo {
                        x = 300
                        y = 50
                    }
                    lineTo {
                        x = 500
                        y = 10
                    }
                }
            }}
        }}
        val stub = showNodeOnStage(path()).get()
        //delay(1_000)
    }

    //@Test
    fun heart() = fxRunTest{
        val heart = {org.drx.evoleq.fx.dsl.fxSvgPath<Nothing> {
            noStub()
            view{configure {
                fill = Color.TRANSPARENT
                stroke = Color.CRIMSON
                strokeWidth = 10.0
                strokeLineJoin = StrokeLineJoin.ROUND
                strokeMiterLimit = 5.0
                elements {
                    moveTo {
                        x = 300
                        y = 400
                    }
                    cubicCurveTo {
                        controlX1 = 300
                        controlY1 = 350
                        controlX2 = 400
                        controlY2 = 400
                        x = 400
                        y = 300
                    }
                    cubicCurveTo {
                        x = 300
                        y = 300
                        controlX2 = 300
                        controlY2 = 250
                    }
                    moveTo {
                        x = 300
                        y = 400
                    }
                    cubicCurveTo {
                        controlX1 = 300
                        controlY1 = 350
                        controlX2 = 200
                        controlY2 = 400
                        x = 200
                        y = 300
                    }
                    cubicCurveTo {
                        x = 300
                        y = 300
                        controlX2 = 300
                        controlY2 = 250
                    }
                    //closePath()
                }
            } }
        }}
        val pane = {fxStackPane<Nothing> {
            noStub()
            view{configure{
                style = "-fx-padding: 10 "
            }}
            child(heart())
        }}
        val stub = showOnStage(pane()).get()

       //delay(1_000)
    }
}