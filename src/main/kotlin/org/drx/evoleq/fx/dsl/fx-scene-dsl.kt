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

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.paint.Paint
import kotlinx.coroutines.*
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxParentComponent
import org.drx.evoleq.fx.component.FxSceneComponent
import org.drx.evoleq.fx.data.FxSceneConfigData
import org.drx.evoleq.fx.data.fxSceneData
import org.drx.evoleq.stub.ParentStubKey
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

class RootStubKey
open class FxSceneComponentConfiguration<R: Parent, D> : Configuration<FxSceneComponent<R, D>> {

    lateinit var stubDef: Stub<D>
    lateinit var idDef: KClass<*>
    lateinit var rootComponentDef: FxParentComponent<R, D>
    var sceneConfigData: FxSceneConfigData = FxSceneConfigData.Empty
    var configure: Scene.()-> Scene = { this }
    var parentalStub: Stub<*>? = null

    var usingStub: Boolean = false

    var rootReady = false

    override fun configure(): FxSceneComponent<R, D> = object: FxSceneComponent<R, D> {

        init{
            if(!usingStub){
                stubDef = object: Stub<D>{
                    override val id: KClass<*>
                        get() = this@FxSceneComponentConfiguration::class
                    override val stubs: HashMap<KClass<*>, Stub<*>>
                        get() = HashMap()
                }
            }
            Parallel<Unit> {
                while(!::stubDef.isInitialized){
                    delay(1)
                }
                stubDef.stubs[RootStubKey::class] = rootComponentDef
                if (parentalStub != null) {
                    rootComponentDef.stubs[ParentStubKey::class] = parentalStub!!
                }
                rootReady = true

                idDef = stubDef.id
            }
            runBlocking{withTimeout(1_000) {
                while (!rootReady) {
                    delay(1)
                }
            }
            }
        }

        override val id: KClass<*>
            get() = this@FxSceneComponentConfiguration.idDef

        override val rootComponent: FxParentComponent<R, D>
            get() = rootComponentDef

        override val sceneData: FxSceneConfigData
            get() = sceneConfigData

        override val configure: Scene.() -> Scene
            get() = this@FxSceneComponentConfiguration.configure

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubDef.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubDef.evolve(d)
    }

    fun root(component: FxParentComponent<R, D>) {
        rootComponentDef = component
    }

    fun sceneConstructorData(
            width: Double? = null,
            height: Double? = null,
            depthBuffer: Boolean? = null,
            antialiasing: SceneAntialiasing? = null,
            fill: Paint? = null) {
        sceneConfigData = fxSceneData(
                width,
                height,
                depthBuffer,
                antialiasing,
                fill)
    }

    fun configure(configuration: Scene.()->Unit) {
        this.configure = {
            this.configuration()
            this
        }
    }

    fun stub(conf: StubConfiguration<D>.()->Unit) {
        usingStub = true
        stubDef = configure(conf)
        idDef = stubDef.id
    }

    fun stub(stub: Stub<D>) {
        usingStub = true
        stubDef = stub
        idDef = stub.id
    }

    fun parentalStub(stub: Stub<*>) {
        parentalStub = stub
    }
}

fun <R: Parent, D> fxScene(configuration: FxSceneComponentConfiguration<R, D>.()->Unit): FxSceneComponent<R, D> = configure(configuration)

