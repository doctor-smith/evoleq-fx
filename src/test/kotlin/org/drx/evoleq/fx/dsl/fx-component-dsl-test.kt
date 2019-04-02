package org.drx.evoleq.fx.dsl

import javafx.scene.Group
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxComponentDslTest {

    @Before fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun children() {
        val c = fxComponent<Group,Nothing> {
            noStub()
            view { configure {  } }
            children {
                item(fxStackPane<Int> {
                    noStub()
                    view { configure {  } }
                })
                child(fxStackPane<Int> {
                    noStub()
                    view { configure {  } }
                })
            }
        }
    }
}
