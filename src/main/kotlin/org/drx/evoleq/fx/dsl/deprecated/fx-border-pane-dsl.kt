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
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.deprecated.FxBorderPaneComponent
import org.drx.evoleq.fx.component.deprecated.FxNodeComponent
import org.drx.evoleq.fx.component.deprecated.FxParentComponent
import org.drx.evoleq.stub.DefaultIdentificationKey
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass
class TopComponentKey
class BottomComponentKey
class RightComponentKey
class LeftComponentKey
class CenterComponentKey
/**
 * @deprecated
 */
/*
open class FxBorderPaneComponentConfiguration<B: BorderPane,D> : FxParentComponentConfiguration<B, D>() {

    var topComponent: FxNodeComponent<*, *>? = null
    var rightComponent: FxNodeComponent<*, *>? = null
    var bottomComponent: FxNodeComponent<*, *>? = null
    var leftComponent: FxNodeComponent<*, *>? = null
    var centerComponent: FxNodeComponent<*, *>? = null

    override fun configure(): FxBorderPaneComponent<B, D> = object: FxBorderPaneComponent<B, D>(){

        init{ component = this }

        override val bottomComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.bottomComponent

        override val centerComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.centerComponent

        override val leftComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.leftComponent

        override val rightComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.rightComponent

        override val topComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.topComponent

        override val node = viewConfiguration

        override val children = childComponents

        override val id: KClass<*>
            get() = this@FxBorderPaneComponentConfiguration.idConfiguration

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubConfiguration.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
    }

    fun <N : Node, D> top(component: FxNodeComponent<N, D>) {
        topComponent = component
        whenStubIsReady {
            Parallel<Unit> {
                var idSet = false
                while(!idSet) {
                    try {
                        val id = component.id
                        idSet = true
                    } catch (exception: Exception) {
                        delay(1)
                    }
                }
                if(component.id == DefaultIdentificationKey::class){
                    stubs[TopComponentKey::class] = component
                } else {
                    stubs[component.id] = component
                }
            }
        }
    }

    fun <N : Node, D> right(component: FxNodeComponent<N, D>) {
        rightComponent = component
        whenStubIsReady {
            Parallel<Unit> {
                var idSet = false
                while(!idSet) {
                    try {
                        val id = component.id
                        idSet = true
                    } catch (exception: Exception) {
                        delay(1)
                    }
                }
                if(component.id == DefaultIdentificationKey::class){
                    stubs[RightComponentKey::class] = component
                } else {
                    stubs[component.id] = component
                }
            }
        }
    }

    fun <N : Node, D> bottom(component: FxNodeComponent<N, D>) {
        bottomComponent = component
        whenStubIsReady {
            Parallel<Unit> {
                var idSet = false
                while(!idSet) {
                    try {
                        val id = component.id
                        idSet = true
                    } catch (exception: Exception) {
                        delay(1)
                    }
                }
                if(component.id == DefaultIdentificationKey::class){
                    stubs[BottomComponentKey::class] = component
                } else {
                    stubs[component.id] = component
                }
            }
        }
    }

    fun <N : Node, D> left(component: FxNodeComponent<N, D>) {
        leftComponent = component
        whenStubIsReady {
            Parallel<Unit> {
                var idSet = false
                while(!idSet) {
                    try {
                        val id = component.id
                        idSet = true
                    } catch (exception: Exception) {
                        delay(1)
                    }
                }
                if(component.id == DefaultIdentificationKey::class){
                    stubs[LeftComponentKey::class] = component
                } else {
                    stubs[component.id] = component
                }
            }
        }
    }

    fun <N : Node, D> center(component: FxNodeComponent<N, D>) {
        centerComponent = component
        whenStubIsReady {
            Parallel<Unit> {
                var idSet = false
                while(!idSet) {
                    try {
                        val id = component.id
                        idSet = true
                    } catch (exception: Exception) {
                        delay(1)
                    }
                }
                if(component.id == DefaultIdentificationKey::class){
                    stubs[CenterComponentKey::class] = component
                } else {
                    stubs[component.id] = component
                }
            }
        }
    }
}

 */

/**
 * @deprecated
 */
//fun <G : BorderPane, D> fxBorderPane(configuration: FxBorderPaneComponentConfiguration<G, D>.()->Unit) : FxParentComponent<G, D> = configure(configuration) as FxBorderPaneComponent<G, D>