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

import javafx.scene.Group
import javafx.scene.control.Button
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.component.FxInputComponent
import org.drx.evoleq.fx.component.asFxComponent
import org.drx.evoleq.fx.component.withInput
import org.drx.evoleq.fx.stub.FxInputPhase
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.stub.findByKey
import org.junit.Test

class FxButtonTest {

    @Test fun basicConfiguration() = runBlocking {
        val button = fxButton<Unit> {
            id<Button>()
            view { configure{} }
            stub(stub{
                evolve{ scope.parallel{ println("evolving...") }}
            })
        }
        button.evolve(Unit).get()
    }

    @Test fun configurationAsSubComponent() = runBlocking {
        val group = fxGroup<Unit>{
            id<Group>()
            view{configure{}}
            child(fxButton{
                id<Button>()
                //noStub()
                view { configure{} }
                stub(stub{})
            })
            stub(stub{})
/*
            fxRunTime{
                assert(children.size == 1)
            }
*/

        }
        delay(2_000)
    }


    @Test fun asInputComponent() = fxRunTest {
        var inputString : String? = null
        val button: FxInputComponent<String, Button,Int> = fxButton<Int> {
            id<Button>()
            onInput<String> {input, data->
                scope.parallel{
                    inputString = input
                    FxInputPhase.Stop(data +1)
                }
            }
            view{configure{}}
        }.withInput()
        Parallel {
            val result = button.evolve(0).get()
            //println(result)
            assert(result == 1)
        }
        Parallel {
            button.input("fuck you")
            delay(500)
            assert(inputString != null)
        }

        val  group = fxGroup<Int>{
            tunnel()
            view{configure{}}
            child(button)
        }

        val b = group.findByKey(Button::class)!!
                .asFxComponent<Button,Int>()
                .withInput<String,Button,Int>()
        Parallel {
            val result = b.evolve(0).get()
            //println(result)
            assert(result == 1)
        }
        Parallel {
            b.input("fuck you")
            delay(500)
            assert(inputString != null)
        }

        delay(2_000)
    }
}