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

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.data.FxSceneConfigData
import org.drx.evoleq.fx.data.toScene
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.stub.Stub
import java.lang.Thread.sleep

interface FxComponent<N, D> : Stub<D> {
    fun show(): N
    suspend fun ready() = true
    fun fxRunTime(action: N.()->Unit) { }
}



interface FxNodeComponent<N: Node, D> : FxComponent<N,D>

abstract class FxParentComponent<P: Parent, D> : FxNodeComponent<P,D> {

    abstract val node: ()->P

    abstract val children: ArrayList<FxNodeComponent<*,*>>

    open fun children(): ObservableList<Node> = node().childrenUnmodifiable

}

abstract class FxGroupComponent<G: Group, D> : FxParentComponent<G, D>() {
    lateinit var n: G
    override fun show(): G {
        var ready = false
        Parallel<Unit>
        {
            ready = ready()
        }
        while(!ready){
            sleep(1)
        }
        n = node()
        n.children.clear()
        children.forEach {
            n.children.add( it.show() )
        }
        return n
    }
    override fun fxRunTime(action: G.() -> Unit) {

        Parallel<Unit> {
            while(!::n.isInitialized){
                kotlinx.coroutines.delay(1)
            }
            ParallelFx<Unit> {
                n.action()
            }
        }
    }
    override fun children(): ObservableList<Node> = node().children
}

abstract class FxPaneComponent<P: Pane, D> : FxParentComponent<P, D>() {
    lateinit var n: P
    override fun show(): P {

        var ready = false
        Parallel<Unit>
        {
            ready = ready()
        }
        while(!ready){
            sleep(1)
        }
        n = node()
        //println("@FxPaneComponent: ${n.children.size}")
        n.children.clear()
        children.forEach {
            n.children.add( it.show() )
        }
        return n
    }

    override fun fxRunTime(action: P.() -> Unit) {

        Parallel<Unit> {
            while(!::n.isInitialized){
                kotlinx.coroutines.delay(1)
            }
            ParallelFx<Unit> {
                n.action()
            }
        }
    }

    override fun children(): ObservableList<Node> = node().children
}

abstract class FxBorderPaneComponent<B : BorderPane, D> : FxParentComponent<B, D>() {
    abstract val topComponent:FxNodeComponent<*,*>?
    abstract val rightComponent:FxNodeComponent<*,*>?
    abstract val bottomComponent:FxNodeComponent<*,*>?
    abstract val leftComponent:FxNodeComponent<*,*>?
    abstract val centerComponent:FxNodeComponent<*,*>?

    lateinit var  n: B
    override fun show(): B {
        var ready = false
        Parallel<Unit>
        {
            ready = ready()
        }
        while(!ready){
            sleep(1)
        }
        //while (node == null)
        //node.children.clear()
        n = node()
        n.children.clear()
        if(topComponent != null) {
            n.top = topComponent!!.show()
        }
        if(rightComponent != null) {
            n.right = rightComponent!!.show()
        }
        if(bottomComponent != null) {
            n.bottom = bottomComponent!!.show()
        }
        if(leftComponent != null) {
            n.left = leftComponent!!.show()
        }
        if(centerComponent != null) {
            n.center = centerComponent!!.show()
        }

        children.forEach {
            n.children.add( it.show() )
        }
        return n
    }

    override fun fxRunTime(action: B.() -> Unit) {

        Parallel<Unit> {
            while(!::n.isInitialized){
                kotlinx.coroutines.delay(1)
            }
            ParallelFx<Unit> {
                n.action()
            }
        }
    }

    override fun children(): ObservableList<Node> = node().children
}

abstract class FxAnchorPaneComponent<A: AnchorPane, D> : FxParentComponent<A, D>() {
    lateinit var n: A
    override fun show(): A {
        var ready = false
        Parallel<Unit>
        {
            ready = ready()
        }
        while(!ready){
            sleep(1)
        }
        n = node()
        n.children.clear()
        children.forEach {
            n.children.add( it.show() )
        }
        return n
    }
    override fun fxRunTime(action: A.() -> Unit) {

        Parallel<Unit> {
            while(!::n.isInitialized){
                kotlinx.coroutines.delay(1)
            }
            ParallelFx<Unit> {
                n.action()
            }
        }
    }
    override fun children(): ObservableList<Node> = node().children
}


interface FxStageComponent<D> : FxComponent<Stage,D>{
    val sceneComponent : FxSceneComponent<*, D>
    val stage: Stage
    val configure: Stage.()->Stage
    override fun show(): Stage {
        var ready = false
        Parallel<Unit>
        {
            ready = ready()
        }
        while(!ready){
            sleep(1)
        }
        val stage = Stage()
        val scene = sceneComponent.show()
        stage.configure().scene = scene
        return stage
    }
    override fun fxRunTime(action: Stage.() -> Unit) {

        Parallel<Unit> {

            ParallelFx<Unit> {
                stage.action()
            }
        }
    }
}

interface FxSceneComponent<R: Parent,D> : FxComponent<Scene, D>{
    val rootComponent: FxParentComponent<R, D>
    val sceneData: FxSceneConfigData
    val configure: Scene.()->Scene

    //var scene: Scene?
    override fun show(): Scene {
        var ready = false
        Parallel<Unit>
        {
            ready = ready()
        }
        while(!ready){
            sleep(1)
        }
        //rootComponent.node.scene.root = null//.children.clear()
        val root: R = rootComponent.show()
        if(rootComponent.node().scene != null){
            return root.toScene(sceneData)
            //return scene!!
        }
        return  root.toScene(sceneData).configure()
        //return scene!!
    }
/*
    override fun fxRunTime(action: Scene.() -> Unit) {

        Parallel<Unit> {
            while(scene == null){
                kotlinx.coroutines.delay(1)
            }
            ParallelFx<Unit> {
                scene!!.action()
            }
        }
    }
    */
}

