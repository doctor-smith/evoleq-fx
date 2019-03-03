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

import javafx.scene.Node
import javafx.scene.Parent
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.fx.component.FxParentComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

open class FxParentComponentConfiguration<P: Parent,D> : FxNodeComponentConfiguration<P, D>() {

    // data related to configuration process
    // states
    var childComponentsReady: Boolean = false
    var readyForCrossConfiguration: Boolean = false

    // timeouts
    val childComponentsTimeout: Long = 1_000
    protected val readyForCrossConfigurationTimeout: Long = 1_000



    protected val childComponents = ArrayList<FxNodeComponent<*, *>>()

    fun<N : Node, E> child(component: FxNodeComponent<N, E>) {
        childComponents.add(component)
    }

    init{
        Parallel<Unit> {
            withTimeout(childComponentsTimeout) {
                println("adding child stubs: using default-stub = ${!usingStub}")
                whenStubIsReady {
                    // add child components as sub-stubs
                    childComponents.forEach {
                        stubs[it.id] = it
                        println("added ${it.id}")
                        //}
                        readyForCrossConfiguration = true
                    }
                }
            }
        }
    }


    override fun configure(): FxParentComponent<P, D> = object: FxParentComponent<P, D>() {

        override val node = viewConfiguration

        override val children = childComponents

        override val id: KClass<*>
            get() = this@FxParentComponentConfiguration.idConfiguration

        override fun show(): P = viewConfiguration()

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubConfiguration.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
    }

    fun whenReady(perform:()->Unit) = Parallel<Unit>{
        withTimeout(readyForCrossConfigurationTimeout) {
            while (!readyForCrossConfiguration) {
                delay(1)
            }
            perform()
        }
    }


}