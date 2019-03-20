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
import org.drx.evoleq.fx.dsl.ID


abstract class SimpleAppManager<D> : AppManager<D>() {

    protected val stages: HashMap<ID, Stage> by lazy {
        HashMap<ID, Stage>()
    }
    protected val stageComponents: HashMap<ID, FxComponent<Stage, *>> by lazy {
        HashMap<ID, FxComponent<Stage,*>>()
    }
    protected val sceneComponents: HashMap<ID, FxComponent<Scene, *>> by lazy {
        HashMap<ID, FxComponent<Scene, *>>()
    }
    protected val nodeComponents: HashMap<ID, FxComponent<Node, *>> by lazy {
        HashMap<ID, FxComponent<Node, *>>()
    }

    protected open fun showStage(id : ID) {
        val stage = stageComponents[id]!!.show()
        stages[id] = stage
        showStage(stage)
    }

    protected open fun hideStage(id: ID) {
        val stage = stages[id]
        if(stage != null){
            stages.remove(id)
            hideStage(stage)
        }
    }
}