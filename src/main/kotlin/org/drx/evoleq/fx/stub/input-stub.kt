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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.drx.evoleq.coroutines.onNext
import org.drx.evoleq.coroutines.suspended
import org.drx.evoleq.dsl.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.math.map
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import kotlin.reflect.KClass


class Input<out I>(val value: I)


private class FxInputStub
sealed class FxInputPhase<D>(open val data: D) {
    data class Start<D>(override val data: D) : FxInputPhase<D>(data)
    data class Wait<D>(override val data: D) : FxInputPhase<D>(data)
    data class Stop<D>(override val data: D) : FxInputPhase< D>(data)
    data class Stopped<D>(override val data: D) : FxInputPhase< D>(data)
}
class InputStub<I, D>(private val onInput: (I, D)-> Evolving<FxInputPhase<D>>) : Stub<D> {
    override val scope: CoroutineScope by lazy{ DefaultStubScope() }

    override val id: KClass<*>
        get() = this::class
    override val stubs: HashMap<KClass<*>, Stub<*>> = HashMap()

    override suspend fun evolve(d: D): Evolving<D> = innerFlow.evolve(FxInputPhase.Start(d)).map(suspended{ phase: FxInputPhase<D> -> phase.data })

    val inputReceiver = CoroutineScope(Job()).receiver<I> {  }
    private val inputStack = arrayListOf<I>()

    init{
        inputReceiver.onNext(scope){input -> inputStack.add(input)}
    }

    var onStart: suspend (D)->D = {d -> d}
    private suspend fun onStartBase(data: D):D  = onStart(data)

    var onStop: suspend (D)->D = {d -> d}
    private suspend fun onStopBase(data: D): D = onStop(data)

    private val innerStub = stub<FxInputPhase<D>>{
        id<FxInputStub>()
        evolve{ phase -> when(phase) {
            is FxInputPhase.Start -> scope.parallel {
                FxInputPhase.Wait( onStartBase( phase.data ) )
            }
            is FxInputPhase.Wait -> {
                inputStack.onNext{ input ->
                    onInput(input, phase.data)
                }
            }
            is FxInputPhase.Stop -> scope.parallel{
                FxInputPhase.Stopped(onStopBase(phase.data))
            }
            is FxInputPhase.Stopped -> scope.parallel{
                phase
            }
        } }
    }
    private val innerFlow = innerStub.toFlow<FxInputPhase<D>, Boolean>(
            conditions{
                testObject(true)
                check{b -> b}
                updateCondition { phase ->
                    if(phase is FxInputPhase.Stopped){
                        try{inputReceiver.actor.close()}catch(ignored: Exception){
                            println("Closing inputReceiver.actor: Error")
                            ignored.stackTrace
                        }
                        try{inputReceiver.channel.close()}catch(ignored: Exception){
                            println("Closing inputReceiver.channel: Error")
                            ignored.stackTrace
                        }
                    }
                    phase !is FxInputPhase.Stopped
                }
            }
    )
}