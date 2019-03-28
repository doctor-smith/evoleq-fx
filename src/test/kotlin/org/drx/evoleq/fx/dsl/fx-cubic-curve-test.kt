package org.drx.evoleq.fx.dsl

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.geometry.Derivation
import org.drx.evoleq.fx.test.showInTestStage
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxCubicCurveTest {
    @Before fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }

    @Test fun basics() = runBlocking{
        val curve = fxCubicCurve<Nothing> {
            id<CubicCurve>()
            view{configure {
                stroke = Color.RED
                fill = null
                start(Derivation(10,10,200,20))
                end(Derivation(250,250,20,-200))
            }}
            stub(stub{})
        }
        val pane = fxStackPane<Nothing> {
            tunnel()
            view{configure {
                prefHeight = 300.0
                prefWidth = 400.0
            }}
            child(curve)
        }
        val stub = showInTestStage(pane).get()
        delay(10_000)
    }
}