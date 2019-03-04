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
package org.drx.evoleq.fx.test

import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.application.SimpleAppManager
import org.drx.evoleq.fx.component.FxStageComponent
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.stub.Stub

/**
 * Show the stageComponent and return it as a stub
 */
fun <D> showTestStage(stageComponent: FxStageComponent<D>): Parallel<Stub<D>> = Parallel {

    class TestApp<D> : SimpleAppManager<D>() {
        init{
            ParallelFx<Unit>{showStage(stageComponent.show())}
        }
        override fun configure(): Stub<D> = stageComponent as Stub<D>
    }
    val stub = AppManager.launch(TestApp<D>()).get()
    stub
}