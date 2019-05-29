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
package org.drx.evoleq.fx.application

import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.test.fxRunTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import java.lang.Exception

class IdProviderTest {
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

    //@Test
    fun test() = fxRunTest{//runBlocking {
        val N = 10000
        val ids = arrayListOf<Parallel<ID>>()
        val provider = idProvider()//IdProvider()
        val process = Parallel<Unit> {
            IntRange(1, N).forEach {
                //Parallel<Unit> {
                    ids.add(Parallel {
                        val property = SimpleObjectProperty<ID>(PreId::class)
                        //provider.add(property)
                        provider.send(property)
                        while(property.value == PreId::class) {
                            delay(1)
                        }
                        property.value
                    })
                //}
            }
        }
        /*
        Parallel<Unit> {
            provider.run()
        }

         */
        delay(1_000)
        //process.get()

        //provider.terminate()
        provider.close()
        //withTimeout(1_000) {
        assert(ids.size == N)

        println("first id: "+ids.first().get())
        //ids.map{println(it.get())}
        //}
        var count = 0
        ids.forEach { try{withTimeout(10){
            val id = it.get()

            count++
            //println(id)
        }}catch(exception : Exception){
            println("@id: $count: error ok: $exception")
        }
        }
        println(count)
        delay(1_000)
    }
}