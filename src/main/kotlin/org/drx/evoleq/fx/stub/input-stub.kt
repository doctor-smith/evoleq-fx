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
import org.drx.evoleq.dsl.*
import org.drx.evoleq.coroutines.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.dsl.Update
import org.drx.evoleq.dsl.Updated
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.math.map
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.drx.evoleq.util.*
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

    val inputReceiver = CoroutineScope(Job()).receiver<I>(capacity = 10_000) {  }
    private val inputStack = smartArrayListOf<I>()

    val updateReceiver = CoroutineScope(Job()).receiver<Update<ID,D>/*suspend D.()->D*/>(capacity = 10_000){ }
    private val updateStack = smartArrayListOf<Update<ID,D>/*suspend D.()->D*/>()

    init{
        inputReceiver.onNext(scope){input -> inputStack.add(input)}
        updateReceiver.onNext(scope){update -> updateStack.add(update)}
    }

    var onStart: suspend (D)->D = {d -> d}
    private suspend fun onStartBase(data: D):D  = onStart(data)

    var onStop: suspend (D)->D = {d -> d}
    private suspend fun onStopBase(data: D): D = onStop(data)

    var onUpdate: suspend Updated<ID,D>.()->D = {data}
    private suspend fun Updated<ID,D>.onUpdateBase(): D = with(this){
        onUpdate()
    }

    private val innerStub = stub<FxInputPhase<D>>{
        id<FxInputStub>()
        evolve{ phase -> when(phase) {
            is FxInputPhase.Start -> scope.parallel {
                FxInputPhase.Wait( onStartBase( phase.data ) )
            }
            is FxInputPhase.Wait -> {
                blockUntil(updateStack.isEmpty and inputStack.isEmpty){value -> !value}
                if(updateStack.isNotEmpty()){
                    updateStack.onNext { update ->
                        scope.parallel {
                            FxInputPhase.Wait(update.update(phase.data).onUpdateBase()/*phase.data.update().onUpdateBase()*/)
                        }
                    }
                } else {
                    inputStack.onNext { input ->
                        onInput(input, phase.data)
                    }
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
                updateCondition { phase -> phase !is FxInputPhase.Stopped }
            }
    )
}