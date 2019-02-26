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
package org.drx.evoleq.fx.component

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.drx.evoleq.fx.data.FxSceneConfigData
import org.drx.evoleq.fx.data.toScene
import org.drx.evoleq.stub.Stub

interface FxComponent<N, D> : Stub<D> {
    fun show(): N
}

interface FxNodeComponent<N: Node, D> : FxComponent<N,D>

abstract class FxParentComponent<P: Parent, D> : FxNodeComponent<P,D> {

    abstract val node: ()->P

    abstract val children: ArrayList<FxNodeComponent<*,*>>

    fun children(): ArrayList<FxNodeComponent<*,*>> = children

}

abstract class FxGroupComponent<G: Group, D> : FxParentComponent<G, D>() {
    override fun show(): G {
        //node.children.clear()
        val n = node()
        n.children.clear()
        children.forEach {
            n.children.add( it.show() )
        }
        return n
    }
}

abstract class FxPaneComponent<P: Pane, D> : FxParentComponent<P, D>() {
    override fun show(): P {
        //node.children.clear()
        val n = node()
        n.children.clear()
        children.forEach {
            n.children.add( it.show() )
        }
        return n
    }
}



interface FxStageComponent<D> : FxComponent<Stage,D>{
    val sceneComponent : FxSceneComponent<*, D>
    val stage: Stage
    val configure: Stage.()->Stage
    override fun show(): Stage {
        val stage = Stage()
        val scene = sceneComponent.show()
        stage.configure().scene = scene
        return stage
    }
}

interface FxSceneComponent<R: Parent,D> : FxComponent<Scene, D>{
    val rootComponent: FxParentComponent<R, D>
    val sceneData: FxSceneConfigData
    val configure: Scene.()->Scene
    override fun show(): Scene {
        //rootComponent.node.scene.root = null//.children.clear()
        val root: R = rootComponent.show()
        if(rootComponent.node().scene != null){
            return root.toScene(sceneData)
        }
        return root.toScene(sceneData).configure()
    }
}

