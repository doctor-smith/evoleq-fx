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

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.junit.Test
import org.testfx.api.FxToolkit
import java.lang.Thread.sleep

class ParallelFxTest {

    @Test
    fun runsOnApplicationThread() = fxRunTest{//runBlocking{
        val parallelFx = ParallelFx<String> {
            assert(FxToolkit.isFXApplicationThreadRunning())
            Thread.currentThread().name
        }
        val threadName = parallelFx.get()

        assert(threadName == "JavaFX Application Thread")
    }

    @Test fun isLaunchedOnInitialization() = fxRunTest{//runBlocking{
        val parallel = ParallelFx<Unit>{
            sleep(1_000)
        }
        delay(1_050)
        val time = System.currentTimeMillis()
        val u = parallel.get()
        val measure = System.currentTimeMillis() - time
        println(measure)
        assert(measure <= 50 )
    }

    @Test fun cancelImmediately() = fxRunTest{//runBlocking {
        var time = System.currentTimeMillis()
        val parallelFx = ParallelFx<Int> {
            sleep(1_000)
            1
        }
        launch{ coroutineScope{
            val x = parallelFx.get()
            assert(x == 0)
        }
        }

        parallelFx.cancel(0).get()
        time = System.currentTimeMillis() - time
        assert(time < 50)
        //println(time)
    }

    @Test fun cancelLate() = fxRunTest{//runBlocking {
        val parallelFx = ParallelFx<Int> {
            sleep(200)
            1
        }
        var x = -1
        launch { coroutineScope{
            x = parallelFx.get()
            assert(x == 0)
        } }
        delay(100)
        x = parallelFx.cancel(0).get()
        assert(x == 0)

    }
}