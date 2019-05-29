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
package org.drx.evoleq.fx.evolving

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.test.fxRunTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import java.lang.Thread.sleep

class ParallelFxTest {
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
    @Test
    fun runsOnApplicationThread() = fxRunTest{//runBlocking{
        val parallelFx = ParallelFx<String> {
            assert(FxToolkit.isFXApplicationThreadRunning())
            Thread.currentThread().name
        }
        val threadName = parallelFx.get()

        assert(threadName == "JavaFX Application Thread")
    }
}