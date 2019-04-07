package org.drx.evoleq.fx.dsl

import javafx.scene.Group
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.test.showInTestStage
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

    @Test fun configureWithConstructorData() = runBlocking {
        var done1: Boolean = false
        var done2: Boolean = false
        val c = fxGroup<Nothing>{
            noStub()
            view{configure{}}
            child(fxButton {
                noStub()
                view{configure("Hello1"){
                    done1 = true
                }}
            })
            child(fxButton {
                noStub()
                view{configure(constructor("Hello2")){
                    done2 = true
                }}
            })
        }
        val stub = showInTestStage(c).get()
        assert(done1 && done2)
        //delay(10_000)
    }
}
