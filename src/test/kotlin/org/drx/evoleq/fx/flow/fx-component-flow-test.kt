package org.drx.evoleq.fx.flow

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.condition.Not
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.test.showTestStage
import org.drx.evoleq.stub.*
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import java.lang.Thread.sleep

class FxComponentFlowTest {
    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }


    @Test fun example() = runBlocking {
        val stageConfiguration = fxComponent<Stage,Nothing> stage@{
            val closeButtonClicked = SimpleBooleanProperty(false)
            id(StageStubKey::class)
            view{
                configure {
                    //initStyle(StageStyle.UNDECORATED)
                    title = "title"

                    setOnCloseRequest { event ->
                        event.consume()
                        closeButtonClicked.value = true
                        shutdown()
                    }
                }
            }
            scene(fxComponent {
                id(SceneStubKey::class)
                root<AnchorPane,Nothing>(fxComponent {
                    id(RootStubKey::class)
                    view{ configure{} }
                    noStub()

                    child<Button,Nothing>(fxComponent{
                        id(Key1::class)
                        view{
                            configure{
                                text = "text"
                                leftAnchor(20)
                                bottomAnchor(10)
                            }
                        }
                        noStub()
                    })
                    child<BorderPane, Nothing>(fxComponent {
                        id<Key2>()
                        view{
                            configure{
                                leftAnchor( 10)
                                bottomAnchor(50.0)
                                topAnchor(10)
                            }
                        }
                        top<StackPane, Nothing>(fxComponent {
                            id<TopId>()
                            view{configure{}}
                            child<Button,Nothing>(fxComponent {
                                id(Key5::class)
                                view{ configure{text = "TOP"}}
                                noStub()
                            })
                            noStub()
                        })
                        bottom<Button, Nothing>(fxComponent {
                            id(BottomId::class)
                            view{configure{ text = "BOTTOM" }}

                            noStub()
                        })

                        noStub()
                    })
                }){
                    root -> Scene(root, 300.0, 200.0)
                }
                noStub()
            })

            fxRunTime {
                title = "FxRunTimeTitle"
            }

            stub(stub{})
            this@stage.stubAction {
                val s = findByKey(SceneStubKey::class)!! as FxComponent<Scene, Nothing>
            }
            //shutdown()
        }

        val stub = showTestStage(stageConfiguration).get()
        //FxRobot().clickOn()
        delay(5_000)
    }

    @Test fun exception() = runBlocking {
        val component = fxComponent<Node, Int> {  }
        //delay(5_000)
    }

}