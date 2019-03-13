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

import kotlinx.coroutines.delay
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.fx.phase.FxComponentPhase

abstract class  FxRunTime<N , D> {
    abstract val view: N
    abstract val component: FxComponent<N, D>
    abstract val phase: FxComponentPhase.RunTimePhase<N,D>
    private val actions: ArrayList<N.() -> Unit> by lazy{ arrayListOf<N.()->Unit>() }

    var shutdown: Boolean = false

    fun fxRun() : Parallel<FxComponentPhase.RunTimePhase<N, D>> = Parallel{
        while(actions.isEmpty()){
            delay(1)
        }
        var nextPhase = phase
        try {
            while(actions.isNotEmpty()) {
                var action = actions.first()
                actions.removeAt(0)
                ParallelFx<Unit> {
                    view.action()
                }.get()
                //

            }
        } catch (exception: Exception) {
            shutdown = true//ShutDown(view, component)
        }
        if(shutdown){
            nextPhase = FxComponentPhase.RunTimePhase.ShutDown<N,D>()
        }
        nextPhase
    }
    fun fxRunTime(action: N.() -> Unit) = Parallel<Unit>{
        actions.add(action)

    }
    fun shutdown() = Parallel<Unit>{
        val action:N.()->Unit = {shutdown = true}//throw Exception("ShutDown")}
        actions.add(0, action)
    }
    /*
    fun runTime(action: FxComponent<N, D>.()->Unit) = Parallel<FxRunTime<N,D>>{
        component.action()
        this@FxRunTime
    }
    */
}