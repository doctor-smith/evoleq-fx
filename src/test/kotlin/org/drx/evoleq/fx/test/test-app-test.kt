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

import javafx.application.Application
import javafx.scene.layout.StackPane
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.test.deprecated.showTestStage
import org.junit.After
import org.junit.Before
import org.testfx.api.FxToolkit
import org.junit.Test

class TestAppTest{
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

    @Test fun testApp() = fxRunTest{//runBlocking{

        val stageComponent = fxStage<Boolean> {
            id<StageId>()
            view{configure{}}
            scene(fxScene {

                root(fxStackPane {
                    view { configure{} }
                    noStub()
                })
                noStub()
            })
            stub(stub {
                evolve { b ->
                    Immediate { !b }
                }
            })
        }

        val stub = showTestStage(stageComponent).get()
        assert(stub.evolve(false).get())
        assert(stageComponent.evolve(false ).get())
        delay(1_000)
    }

    @Test fun testApp2() = fxRunTest{//runBlocking{

        val stageComponent = fxStage<Boolean> {
            id<StageId>()
            view{configure{}}
            scene(fxScene {
                noStub()
                root(fxStackPane {
                    noStub()
                    view { configure{} }

                })

            })
            stub(stub {
                evolve { b ->
                    Immediate { !b }
                }
            })
        }

        val stub = launchTestStage(stageComponent).get()
        assert(stub.evolve(false).get())
        assert(stageComponent.evolve(false ).get())
        delay(1_000)
    }
}

