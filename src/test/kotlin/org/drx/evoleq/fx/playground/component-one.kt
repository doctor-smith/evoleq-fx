package org.drx.evoleq.fx.playground

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.effect.Reflection
import javafx.scene.layout.AnchorPane
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.fx.test.showTestStage
import org.drx.evoleq.stub.Key50
import org.drx.evoleq.stub.Key9
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class ComponentOneTest {
    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun go() = runBlocking {
        val stageComponent = fxStage<Nothing> {
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
                        stub(NoStub())
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
                            stub(NoStub())
                        })
                   })
                }){
                    root -> Scene(root, 350.0,230.0)
                }
                noStub()

            })
            stub(NoStub())
        }

        val stub = showTestStage(stageComponent).get()

        delay(10_000)
    }
}