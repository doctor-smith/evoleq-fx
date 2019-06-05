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

import javafx.scene.Parent
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.deprecated.FxSceneComponent
import org.drx.evoleq.fx.component.deprecated.FxStageComponent
import org.drx.evoleq.stub.ParentStubKey
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

/**
 * Key of the sceneComponents stub.
 * Automatically set during setup
 */
class StageStubKey
class SceneStubKey
/**
 * @deprecated
 */
open class FxStageComponentConfiguration<D> : Configuration<FxStageComponent<D>> {

    private lateinit var idDef: KClass<*>
    private val stage: Stage by lazy{ Stage() }
    private lateinit var sceneComponentDef: FxSceneComponent<*, D>
    lateinit var stubDef: Stub<D>
    var parentalStub: Stub<*>? = null

    var configure: Stage.()-> Stage = { this }

    var usingStub: Boolean = false

    private var ready: Boolean = false
    private var readyTimeout: Long = 1_000

    lateinit var component: FxStageComponent<D>
    val componentInitializedTimeout: Long = 1_000

    override fun configure(): FxStageComponent<D> = object: FxStageComponent<D> {

        init{
            if(!usingStub){
                stubDef = object: Stub<D>{
                    override val id: KClass<*>
                        get() = StageStubKey::class
                    override val stubs: HashMap<KClass<*>, Stub<*>>
                        get() = HashMap()
                }
            }
            Parallel<Unit> {
                while (!::stubDef.isInitialized) {
                    delay(1)
                }
                //if(sceneComponentDef.id == DefaultIdentificationKey::class) {
                    stubDef.stubs[SceneStubKey::class] = sceneComponentDef
                //} else {
                //    stubDef.stubs[sceneComponentDef.id] = sceneComponentDef
                //}
                if (parentalStub != null) {
                    sceneComponentDef.stubs[ParentStubKey::class] = parentalStub!!
                }
                idDef = stubDef.id
                ready = true
            }
            component = this
        }

        override val configure: Stage.() -> Stage
            get() = this@FxStageComponentConfiguration.configure
        override val sceneComponent: FxSceneComponent<*, D>
            get() = this@FxStageComponentConfiguration.sceneComponentDef
        override val stage: Stage
            get() = this@FxStageComponentConfiguration.stage
        override val id: KClass<*>
            get() = this@FxStageComponentConfiguration.idDef
        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubDef.stubs

        override suspend fun ready(): Boolean = Parallel<Boolean>{
            performs.map{it.get()}
            true
        }.get()

        override suspend fun evolve(d: D): Evolving<D> = stubDef.evolve(d)
    }

    /**
     * scene configuration
     */
    fun <R : Parent> scene(component : FxSceneComponent<R, D>) {
        this.sceneComponentDef = component
    }

    /**
     * Configure the properties of the stage
     */
    fun configure(configuration: Stage.()->Unit) {
        configure = {
            this.configuration()
            this
        }
    }

    /**
     * Configure the components stub.
     */
    fun stub(conf: StubConfiguration<D>.()->Unit) {
        val stubConf = StubConfiguration<D>()
        usingStub = true
        stubDef = configure(conf)
        idDef = stubDef.id
    }

    fun parentalStub(stub: Stub<*>) {
        parentalStub = stub
    }


    fun stub(stub: Stub<D>) {
        usingStub = true
        stubDef = stub
        idDef = stub.id
    }

    private val performs: ArrayList<Parallel<*>> by lazy{ arrayListOf<Parallel<*>>() }
    fun <T> whenReady(perform: ()->T) = Parallel<T> {
        //performs.add(this@Parallel)
        withTimeout(readyTimeout) {
            while (!ready) {
                delay(1)
            }
            perform()
        }
    }

    fun fxRunTime(perform: Stage.()->Unit) : Parallel<Unit> = Parallel {
        while (!::component.isInitialized ) {
            delay(1)
        }
        component.fxRunTime(perform)
    }
    fun whenComponentReady(perform: FxStageComponent<D>.()->Unit): Parallel<Unit> = Parallel{
        withTimeout(componentInitializedTimeout) {
            while (!::component.isInitialized && !component.ready()) {
                delay(1)

            }
            component.perform()
        }
    }
}

/**
 * Configure an FxStageComponent
 * @deprecated
 */
fun <D> fxStage(configuration: FxStageComponentConfiguration<D>.()->Unit): FxStageComponent<D> = configure(configuration)
