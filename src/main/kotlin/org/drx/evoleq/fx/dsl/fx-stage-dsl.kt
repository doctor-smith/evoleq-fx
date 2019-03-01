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
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxSceneComponent
import org.drx.evoleq.fx.component.FxStageComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass


open class FxStageComponentConfiguration<D> : Configuration<FxStageComponent<D>> {

    private lateinit var idDef: KClass<*>
    private val stage: Stage by lazy{ Stage() }
    private lateinit var sceneComponentDef: FxSceneComponent<*, D>
    lateinit var stubDef: Stub<D>
    var configure: Stage.()-> Stage = { this }
    private var initStyleSet: Boolean = false
    private var initModalitySet: Boolean = false
    private var initOwnerSet: Boolean = false



    override fun configure(): FxStageComponent<D> = object: FxStageComponent<D> {

        init{
            waitForData()
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

        override suspend fun evolve(d: D): Evolving<D> = stubDef.evolve(d)
    }

    fun waitForData() = runBlocking {
        GlobalScope.launch{
            while (!(::sceneComponentDef.isInitialized &&
                    ::stubDef.isInitialized &&
                    ::idDef.isInitialized)) {
                delay(1)
            }
        }
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
     * Configure the components stub
     */
    fun stub(conf: StubConfiguration<D>.()->Unit) {
        stubDef = configure(conf)
        idDef = stubDef.id
    }

    fun stub(stub: Stub<D>) {
        stubDef = stub
        idDef = stub.id
    }
}

/**
 * Configure an FxStageComponent
 */
fun <D> fxStage(configuration: FxStageComponentConfiguration<D>.()->Unit): FxStageComponent<D> = configure(configuration)
