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
package org.drx.evoleq.fx.phase

import kotlinx.coroutines.delay
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponentConfiguration
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.Stub

class ComponentPhaseLauncher<N,D> {
    var id: ID? = null
    var stub: Stub<D>? = null
    var view: (()->N)? = null
    val fxChildren = ArrayList<Parallel<FxComponent<*, *>>>()
    val fxSpecials = ArrayList<Parallel<FxComponent<*, *>>>()
    val stubActions = ArrayList<Parallel<Stub<D>.() -> Unit>>()
    val fxRunTime = ArrayList<Parallel<N.() -> Unit>>()

    fun launch(configuration: FxComponentConfiguration<N, D>): FxComponentPhase.Launch<N,D> = FxComponentPhase.Launch(
            configuration = Parallel{configuration},
            stub = Parallel{
                while( stub == null ) {
                    delay(1)
                }
                stub!!
            },
            view = Parallel{
                while( view == null ) {
                    delay(1)
                }
                view!!
            },
            id = Parallel{
                while( id == null  ) {
                    delay(1)
                }
                id!!
            },
            fxChildren = fxChildren,
            fxSpecials = fxSpecials,
            stubActions = stubActions
    )
}