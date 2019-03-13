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

import javafx.scene.Node
import javafx.scene.Scene
import javafx.stage.Stage
import org.drx.evoleq.fx.component.FxComponent
import kotlin.reflect.KClass


abstract class SimpleAppManager<D> : AppManager<D>() {

    protected val stages: HashMap<KClass<*>, Stage> by lazy {
        HashMap<KClass<*>, Stage>()
    }
    protected val stageComponents: HashMap<KClass<*>, FxComponent<Stage, *>> by lazy {
        HashMap<KClass<*>, FxComponent<Stage,*>>()
    }
    protected val sceneComponents: HashMap<KClass<*>, FxComponent<Scene, *>> by lazy {
        HashMap<KClass<*>, FxComponent<Scene, *>>()
    }
    protected val nodeComponents: HashMap<KClass<*>, FxComponent<Node, *>> by lazy {
        HashMap<KClass<*>, FxComponent<Node, *>>()
    }

    protected open fun showStage(id : KClass<*>) {
        val stage = stageComponents[id]!!.show()
        stages[id] = stage
        showStage(stage)
    }

    protected open fun hideStage(id: KClass<*>) {
        val stage = stages[id]
        if(stage != null){
            stages.remove(id)
            hideStage(stage)
        }
    }
}