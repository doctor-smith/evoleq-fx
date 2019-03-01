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

import javafx.stage.Stage
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.fx.component.FxSceneComponent
import org.drx.evoleq.fx.component.FxStageComponent
import kotlin.reflect.KClass


abstract class SimpleAppManager<D> : AppManager<D>() {

    protected val stages: HashMap<KClass<*>, Stage> by lazy {
        HashMap<KClass<*>, Stage>()
    }
    protected val stageComponents: HashMap<KClass<*>, FxStageComponent<*>> by lazy {
        HashMap<KClass<*>, FxStageComponent<*>>()
    }
    protected val sceneComponents: HashMap<KClass<*>, FxSceneComponent<*, *>> by lazy {
        HashMap<KClass<*>, FxSceneComponent<*, *>>()
    }
    protected val nodeComponents: HashMap<KClass<*>, FxNodeComponent<*, *>> by lazy {
        HashMap<KClass<*>, FxNodeComponent<*, *>>()
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