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
import javafx.scene.layout.AnchorPane
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxAnchorPaneComponent
import org.drx.evoleq.fx.component.FxParentComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

open class FxAnchorPaneComponentConfiguration<A: AnchorPane, D> : FxParentComponentConfiguration<A, D> () {

    override fun configure(): FxAnchorPaneComponent<A, D> = object: FxAnchorPaneComponent<A, D>() {

        override val node = viewConfiguration

        override val children = childComponents

        override val id: KClass<*>
            get() = this@FxAnchorPaneComponentConfiguration.idConfiguration

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubConfiguration.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
    }

    fun <N : Node> N.leftAnchor(  distance: Double) {
        AnchorPane.setLeftAnchor(this,distance)
    }

    fun <N : Node> N.topAnchor(  distance: Double) {
        AnchorPane.setTopAnchor(this,distance)
    }

    fun <N : Node> N.rightAnchor(  distance: Double) {
        AnchorPane.setRightAnchor(this,distance)
    }

    fun <N : Node> N.bottomAnchor(  distance: Double) {
        AnchorPane.setBottomAnchor(this,distance)
    }

}

fun <A: AnchorPane, D> fxAnchorPane(configuration: FxAnchorPaneComponentConfiguration<A, D>.()->Unit): FxParentComponent<A, D> = configure(configuration) as FxAnchorPaneComponent<A, D>