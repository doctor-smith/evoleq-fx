package org.drx.evoleq.fx.component

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.dsl.deprecated.SceneStubKey
import org.drx.evoleq.fx.dsl.deprecated.StageStubKey
import org.drx.evoleq.fx.test.showTestStage
import org.drx.evoleq.stub.*
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class TabPaneTest {
    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun basics() = runBlocking {
        val stageComponent = fxStage<Nothing>{
            id<StageId>()
            view{configure{}}
            scene(fxScene {
                id<SceneId>()
                root(fxTabPane {
                    id<Key1>()
                    view{configure{}}
                    tab(fxTab {
                        id<Key2>()
                        view{configure{

                        }}
                        content(fxButton<Nothing> {
                            id<Key3>()
                            view{configure{
                                text = "BUTTON"
                            }.style("""-fx-color: crimson;""")}
                            noStub()
                        })
                        noStub()
                    })
                    tab(fxTab{
                        id<Key4>()
                        view{configure{}}
                        content(fxButton<Nothing> {
                            id<Key5>()
                            view{configure{
                                text = "BUTTON1"
                            }.style("""-fx-color: crimson;""")}
                            noStub()
                        })
                        noStub()
                    })
                    noStub()
                }){
                    root -> Scene(root, 300.0, 400.0)
                }
                noStub()
            })
            noStub()
        }

        val stub = showTestStage(stageComponent).get()

        delay(5_000)
    }
}