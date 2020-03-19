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
package org.drx.evoleq.fx.stub

import kotlinx.coroutines.*
import org.drx.evoleq.dsl.DefaultSender
import org.drx.evoleq.dsl.Update
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.junit.Test

sealed class Input {
    data class Action(val x: Int): Input()
    object Stop : Input()
}

class InputStubTest {

    @Test
    fun update() = fxRunTest {
        var receivedInput = false
        var receivedUpdate = false
        data class Data(val x : Int)
        val scope = CoroutineScope(Job())
        val inputStub = InputStub { input: Input, data: Data -> when(input) {
            is Input.Action -> scope.parallel {
                println("received input")
                receivedInput = true
                //delay(100)
                if(data.x > 99){
                    FxInputPhase.Wait(Data(data.x + input.x))
                } else {
                    FxInputPhase.Wait(Data(input.x))
                }
            }

            is Input.Stop -> scope.parallel {
                FxInputPhase.Stop(data)
            }
        }
        }
        println("input-stub set up")
        inputStub.onUpdate = {
            println("received update")
            receivedUpdate = true
            this.data
        }

        lateinit var result: Evolving<Data>
        scope.parallel{
            result = inputStub.evolve(Data(0))
        }
        scope.parallel {
            delay(10)
            inputStub.updateReceiver.send ( Update(DefaultSender::class){ Data(1)} )
            delay(20)
            inputStub.updateReceiver.send (Update(DefaultSender::class){ Data(100) })
        }
        scope.parallel{
            (1..20).forEach {
                delay(10)
                inputStub.inputReceiver.send(Input.Action(it))
            }
            delay(2_000)
            inputStub.inputReceiver.send(Input.Stop)
        }




        delay(2_500)
        with(result.get()){
            println(this)
            assert(receivedInput)
            assert(receivedUpdate)
            assert(this.x >= 100)
        }
    }
}