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
package org.drx.evoleq.fx.application.configration

import javafx.scene.Node
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.dsl.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponents
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.dsl.parallelFx
import org.drx.evoleq.fx.phase.AppFlowMessage
import org.drx.evoleq.stub.Stub

abstract class SimpleAppManager<D> : AppManager<D>() {

    lateinit var applicationManagerPort: BaseReceiver<AppFlowMessage.Runtime<D>>
    lateinit var port: BaseReceiver<AppFlowMessage<D>>
    protected lateinit var receiverStub: Stub<AppFlowMessage<D>>
    init{

            scope.parallel<Unit> {parallel{
                port = receiver {}
                receiverStub = receivingStub<AppFlowMessage<D>, AppFlowMessage<D>> {
                    id(AppManager::class)
                    evolve {
                        Parallel { it }
                    }
                    gap {
                        from { message -> Parallel { message } }
                        to { _, message -> Parallel { message } }
                    }
                    receiver(port)
                }
            }.get() }


    }

    protected val stages: HashMap<ID, Stage> by lazy {
        HashMap<ID, Stage>()
    }
    protected val stageComponents: HashMap<ID, suspend CoroutineScope.(CoroutineScope)->FxComponent<Stage, *>> by lazy {
        HashMap<ID, suspend CoroutineScope.(CoroutineScope)->FxComponent<Stage, *>>()
    }
    protected val sceneComponents: HashMap<ID, CoroutineScope.(CoroutineScope)->FxComponent<Scene, *>> by lazy {
        HashMap<ID, CoroutineScope.(CoroutineScope)->FxComponent<Scene, *>>()
    }
    protected val nodeComponents: HashMap<ID, CoroutineScope.(CoroutineScope)->FxComponent<Node, *>> by lazy {
        HashMap<ID, CoroutineScope.(CoroutineScope)->FxComponent<Node, *>>()
    }

    fun registerComponents(components: FxComponents) {
        stageComponents.putAll(components.stages)
        sceneComponents.putAll(components.scenes)
        nodeComponents.putAll(components.nodes)
    }

    open fun showStage(id : ID): Evolving<FxComponent<Stage, *>> = scope.parallel {
            val component = stageComponents[id]!!(scope)
            parallelFx {
                val stage = component.show()
                stages[id] = stage
                showStage(stage)
            }
            component
        }

    open fun hideStage(id: ID) =
        scope.parallelFx {
            val stage = stages[id]
            if (stage != null) {
                stages.remove(id)
                hideStage(stage)
            }
        }
}