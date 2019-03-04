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

