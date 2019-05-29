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

import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.geometry.Derivation
import org.drx.evoleq.fx.test.fxRunTest
import org.drx.evoleq.fx.test.showInTestStage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxCubicCurveTest {
    var m : Application? = null
    @Before
    fun launchBgAppManager() = fxRunTest{//runBlocking {
        FxToolkit.registerPrimaryStage()
        m = FxToolkit.setupApplication { BgAppManager() }
    }
    @After
    fun cleanUp() = fxRunTest{// {
        FxToolkit.cleanupApplication(m!!)
        FxToolkit.cleanupStages()
    }
    @Test fun basics() = fxRunTest{//runBlocking{
        val curve = {fxCubicCurve<Nothing> {
            id<CubicCurve>()
            view{configure {
                stroke = Color.RED
                fill = null
                start(Derivation(10,10,200,20))
                end(Derivation(250,250,20,-200))
            }}
            stub(stub{})
        }}
        val pane = fxStackPane<Nothing> {
            id<Pane>()
            view{configure {
                prefHeight = 300.0
                prefWidth = 400.0
            }}
            child(curve())
            stub(stub{})
        }
        val stub = showInTestStage(pane).get()
        delay(1_000)
    }
}