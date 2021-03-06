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
package org.drx.evoleq.fx.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.drx.evoleq.coroutines.blockWhileEmpty
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.dsl.parallelFx
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.fx.exception.FxConfigurationException
import org.drx.evoleq.fx.phase.FxComponentPhase
/* TODO add dependency on a coroutine-scope*/
abstract class  FxRunTime<N , D> {
    abstract val view: N
    abstract val component: FxComponent<N, D>
    abstract val phase: FxComponentPhase.RunTimePhase<N,D>
    abstract val scope: CoroutineScope
    private val actions: ArrayList<N.() -> Unit> by lazy{ arrayListOf<N.()->Unit>() }

    var shutdown: Boolean = false

    fun fxRun() : Parallel<FxComponentPhase.RunTimePhase<N, D>> = scope.parallel{

        var nextPhase = phase
        //actions.blockWhileEmpty()

        while(actions.isEmpty()){
            delay(1)
        }

        try { parallelFx<Unit> {
            while(actions.isNotEmpty() && !shutdown) {
                val action = actions.first()
                actions.removeAt(0)
                view.action()
            }}.get()
        } catch (exception: Exception) {
            val n: Any = view!!
            phase.errors.add(FxConfigurationException.FxRunTime(component.id, this@FxRunTime, n::class,  exception))
            phase.errors.add(exception)
            shutdown = true
            //shutdown()
        }
        if(shutdown){
            nextPhase = FxComponentPhase.RunTimePhase.ShutDown(phase.log)
            nextPhase.errors.addAll( phase.errors )
        }
        nextPhase
    }
    fun fxRunTime(action: N.() -> Unit) {
        actions.add(action)
    }
    fun shutdown() = scope.parallel<Unit>{
        val action:N.()->Unit = {shutdown = true}
        //val toRemove = arrayListOf<ID>()
        /*
        component.stubs.forEach {
            val stub = it.value
            if(stub is FxComponent<*,*>){
                //toRemove.add(it.key)
                stub.stop()
            }
        }

         */
        /*
        toRemove.forEach {
            component.stubs.remove(it)
        }

         */
        actions.add(0, action)
    }
}