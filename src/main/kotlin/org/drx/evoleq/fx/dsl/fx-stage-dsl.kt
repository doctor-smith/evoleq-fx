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
import javafx.stage.Stage
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxSceneComponent
import org.drx.evoleq.fx.component.FxStageComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass


open class FxStageComponentConfiguration<D> : Configuration<FxStageComponent<D>> {

    private lateinit var id: KClass<*>
    private val stage: Stage by lazy{ Stage() }
    private lateinit var sceneComponent: FxSceneComponent<*, D>
    lateinit var stub: Stub<D>
    var configure: Stage.()-> Stage = { this }



    override fun configure(): FxStageComponent<D> = object: FxStageComponent<D> {
        override val configure: Stage.() -> Stage
            get() = this@FxStageComponentConfiguration.configure
        override val sceneComponent: FxSceneComponent<*, D>
            get() = this@FxStageComponentConfiguration.sceneComponent
        override val stage: Stage
            get() = this@FxStageComponentConfiguration.stage
        override val id: KClass<*>
            get() = this@FxStageComponentConfiguration.id
        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stub.stubs

        override suspend fun evolve(d: D): Evolving<D> = stub.evolve(d)
    }

    fun <R : Parent> scene(component : FxSceneComponent<R, D>) {
        this.sceneComponent = component
    }

    fun configure(configuration: Stage.()->Unit) {
        configure = {
            this.configuration()
            this
        }
    }

    fun stub(conf: StubConfiguration<D>.()->Unit) {
        stub = configure(conf)
        id = stub.id
    }
}
fun <D> fxStage(configuration: FxStageComponentConfiguration<D>.()->Unit): FxStageComponent<D> = configure(configuration)
