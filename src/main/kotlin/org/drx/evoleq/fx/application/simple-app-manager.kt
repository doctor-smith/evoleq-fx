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
package org.drx.evoleq.fx.application

import com.sun.org.apache.xml.internal.security.Init.isInitialized
import javafx.scene.Node
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.dsl.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponents
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.fx.phase.AppFlowMessage
import org.drx.evoleq.stub.Stub

abstract class SimpleAppManager<D> : AppManager<D>() {

    lateinit var applicationManagerPort: BaseReceiver<AppFlowMessage.Runtime<D>>
    lateinit var port: BaseReceiver<AppFlowMessage<D>>
    protected lateinit var receiverStub: Stub<AppFlowMessage<D>>
    init{
        runBlocking {
            Parallel<Unit> {
                port = scope.receiver {}
                receiverStub = receivingStub<AppFlowMessage<D>, AppFlowMessage<D>> {
                    id(AppManager::class)
                    evolve {
                        Immediate { it }
                    }
                    gap {
                        from { message -> Immediate { message } }
                        to { _, message -> Immediate { message } }
                    }
                    receiver(port)
                }
            }.get()
        }
    }

    protected val stages: HashMap<ID, Stage> by lazy {
        HashMap<ID, Stage>()
    }
    protected val stageComponents: HashMap<ID, ()->FxComponent<Stage, *>> by lazy {
        HashMap<ID, ()->FxComponent<Stage,*>>()
    }
    protected val sceneComponents: HashMap<ID, ()->FxComponent<Scene, *>> by lazy {
        HashMap<ID, ()->FxComponent<Scene, *>>()
    }
    protected val nodeComponents: HashMap<ID, ()->FxComponent<Node, *>> by lazy {
        HashMap<ID, ()->FxComponent<Node, *>>()
    }

    fun registerComponents(components: FxComponents) {
        stageComponents.putAll(components.stages)
        sceneComponents.putAll(components.scenes)
        nodeComponents.putAll(components.nodes)
    }

    open fun showStage(id : ID): Evolving<FxComponent<Stage, *>> = ParallelFx<FxComponent<Stage, *>> {
            val component = stageComponents[id]!!()
            val stage = component.show()
            stages[id] = stage
            showStage(stage)
            component
        }

    open fun hideStage(id: ID) =
        ParallelFx<Unit> {
            val stage = stages[id]
            if (stage != null) {
                stages.remove(id)
                hideStage(stage)
            }
        }





}