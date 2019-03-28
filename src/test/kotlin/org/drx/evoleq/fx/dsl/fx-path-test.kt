package org.drx.evoleq.fx.dsl

import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.paint.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.geometry.Derivation
import org.drx.evoleq.fx.test.showTestStage
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import java.nio.file.Path

class FxPathTest {

    @Before fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun fxPath() = runBlocking {
        val stageComponent = fxStage<Unit> {
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
        }

        val stub = showTestStage(stageComponent).get()
        delay(20_000)
    }
}