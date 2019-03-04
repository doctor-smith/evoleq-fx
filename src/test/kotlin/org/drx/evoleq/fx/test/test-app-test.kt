package org.drx.evoleq.fx.test

import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.fxPane
import org.drx.evoleq.fx.dsl.fxScene
import org.drx.evoleq.fx.dsl.fxStage
import org.junit.Before
import org.testfx.api.FxToolkit
import org.junit.Test

class TestAppTest{
    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun testApp() = runBlocking{

        val stageComponent = fxStage<Boolean>{
            scene(fxScene<StackPane,Boolean>{
                root(fxPane {
                    view{node<StackPane>()}
                })
            })
            stub{
                evolve{
                    b -> Immediate{!b}
                }
            }
        }

        val stub = showTestStage(stageComponent).get()
        assert(stub.evolve(false).get())
        assert(stageComponent.evolve(false ).get())
        delay(1_000)
    }
}

