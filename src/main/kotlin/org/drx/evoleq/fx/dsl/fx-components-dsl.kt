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
import javafx.scene.Scene
import javafx.stage.Stage
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.fx.component.FxComponent

data class FxComponents(
    val stages: HashMap<ID,()-> FxComponent<Stage, *>>,
    val scenes: HashMap<ID,()-> FxComponent<Scene, *>>,
    val nodes: HashMap<ID,()-> FxComponent<Node, *>>
)

open class FxComponentsConfiguration : Configuration<FxComponents> {
    private val stages: HashMap<ID,()-> FxComponent<Stage, *>> = hashMapOf()
    private val scenes: HashMap<ID,()-> FxComponent<Scene, *>> = hashMapOf()
    private val nodes: HashMap<ID,()-> FxComponent<Node, *>> = hashMapOf()

    fun stage(id: ID, stage: ()-> FxComponent<Stage, *>){
        stages[id] = stage
    }

    fun scene(id: ID, scene: ()-> FxComponent<Scene, *>){
        scenes[id] = scene
    }
    
    fun node(id: ID, node: ()-> FxComponent<Node, *>){
        nodes[id] = node
    }

    override fun configure(): FxComponents = FxComponents(
        stages,
        scenes,
        nodes
    )
}

fun fxComponents(configuration: FxComponentsConfiguration.()->Unit): FxComponents = configure(configuration)