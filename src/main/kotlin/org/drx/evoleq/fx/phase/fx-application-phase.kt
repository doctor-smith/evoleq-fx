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

import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.flow.Phase
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.application.SimpleAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponents
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass



const val  FX_APPLICATION_PHASE_TIMEOUT: Long = 1_000

sealed class AppFlowMessage<D>{
    class AppStubLaunched<D>(val stub: Stub<D>): AppFlowMessage<D>()
    class ComponentsRegistered<D>: AppFlowMessage<D>()
    sealed class Runtime<D> : AppFlowMessage<D>(){
        class EnteredRuntimePhase<D>(val receiver: BaseReceiver<Runtime<D>>) : Runtime<D>()
        class Wait<D> : Runtime<D>()

        class ShowStage<D>(val id: ID) : Runtime<D>()
        class HideStage<D>(val id: ID) : Runtime<D>()
        sealed class Confirm<D> : Runtime<D>() {
            class Cancel<D>: Confirm<D>()
            class Ok<D> : Confirm<D>()
        }
        //class Stub<D>(val stub: Stub<*>) : Runtime<D>()
        class Terminate<D> : Runtime<D>()
    }
    class FxComponentShown<D>(val component: FxComponent<*,*>) : AppFlowMessage<D>()
    class FxComponentHidden<D>(val id: ID): AppFlowMessage<D>()
    class DriveStub<D>(val stub: Stub<D>) : AppFlowMessage<D>()
    class Terminated<D> : AppFlowMessage<D>()
}

sealed class FxApplicationMessage<D>{
    class Flow<D>(message: AppFlowMessage<D>): FxApplicationMessage<D>()
}

/* TODO error handling */
sealed class FxApplicationPhase<D>(open val errors: ArrayList<Exception> = arrayListOf()) : Phase {
    val defaultTimeout: Long = FX_COMPONENT_PHASE_TIMEOUT

    /**
     * Collect all necessary data and launch the application
     */
    class Launch<D>(
            val applicationClass: KClass<out AppManager<D>>,
            val components: FxComponents,
            val receiver: BaseReceiver<AppFlowMessage<D>>
            //val laterAppStub: Later<Stub<D>>
    ): FxApplicationPhase<D>()

    /**
     * Register components, stylesheets, etc
     */
    sealed class Configure<D>(): FxApplicationPhase<D>() {
        class RegisterComponents<D>(
                val applicationManager: SimpleAppManager<D>,
                val components: FxComponents,
                val receiver: BaseReceiver<AppFlowMessage<D>>
        ) : Configure<D>()

    }

    /**
     * Run the application flow
     */
    class RunTime<D>(
            val applicationManager: SimpleAppManager<D>,
            val applicationManagerPort: BaseReceiver<AppFlowMessage.Runtime<D>>,
            val receiver: BaseReceiver<AppFlowMessage<D>>
    ): FxApplicationPhase<D>()

    /**
     * Terminate application
     */
    class Terminate<D>(val receiver: BaseReceiver<AppFlowMessage<D>>): FxApplicationPhase<D>()
}