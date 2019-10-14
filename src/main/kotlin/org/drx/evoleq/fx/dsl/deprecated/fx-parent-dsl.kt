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
package org.drx.evoleq.fx.dsl.deprecated

import javafx.scene.Node
import javafx.scene.Parent
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.deprecated.FxNodeComponent
import org.drx.evoleq.fx.component.deprecated.FxParentComponent
import org.drx.evoleq.stub.DefaultIdentificationKey
import org.drx.evoleq.stub.Keys
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass
/**
 * @deprecated
 */
/*
open class FxParentComponentConfiguration<P: Parent,D> : FxNodeComponentConfiguration<P, D>() {

    // data related to configuration process
    // states
    var childComponentsReady: Boolean = false
    var readyForCrossConfiguration: Boolean = false

    // timeouts
    val childComponentsTimeout: Long = 1_000
    protected val readyForCrossConfigurationTimeout: Long = 1_000



    protected val childComponents = ArrayList<FxNodeComponent<*, *>>()

    protected val usedDefaultKeys: ArrayList<KClass<*>> by lazy { arrayListOf<KClass<*>>() }

    fun<N : Node, E> child(component: FxNodeComponent<N, E>) {
        childComponents.add(component)
    }
    private fun isDefaultLId(key: KClass<*>): Boolean = Keys.containsValue(key)
    private fun nextDefaultKey(keys: ArrayList<KClass<*>>): KClass<*> = Keys.values.first{!keys.contains(it)}
    init{
        Parallel<Unit> {
            withTimeout(childComponentsTimeout) {
                whenStubIsReady {
                    // add child components as sub-stubs
                    childComponents.forEach {
                        Parallel<Unit> {
                            var idSet = false
                            while(!idSet) {
                                try {
                                    val id = it.id
                                    idSet = true
                                } catch (exception: Exception) {
                                    kotlinx.coroutines.delay(1)
                                }
                            }
                            if (it.id == DefaultIdentificationKey::class) {
                                val key = nextDefaultKey(usedDefaultKeys)
                                stubs[key] = it
                            } else {
                                stubs[it.id] = it
                            }
                            //}
                            readyForCrossConfiguration = true
                        }
                    }
                }
            }
        }

    }


    override fun configure(): FxParentComponent<P, D> = object: FxParentComponent<P, D>() {

        init{ component = this }

        override val node = viewConfiguration

        override val children = childComponents

        override val id: KClass<*>
            get() = this@FxParentComponentConfiguration.idConfiguration

        override fun show(): P = viewConfiguration()

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubConfiguration.stubs

        override suspend fun ready(): Boolean = Parallel<Boolean>{
            performs.map{it.get()}
            true
        }.get()

        override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
    }


    private val performs: ArrayList<Parallel<*>> by lazy{ arrayListOf<Parallel<*>>() }
    fun <T> whenReady(perform:()->T) = Parallel<T>{
        //performs.add(this@Parallel)
        withTimeout(readyForCrossConfigurationTimeout) {
            while (!readyForCrossConfiguration) {
                delay(1)
            }
            perform()
        }
    }




}

 */