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

import org.drx.evoleq.dsl.ConfigurationPhase
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.flow.Phase
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponentConfiguration
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.stub.Stub

const val  FX_COMPONENT_PHASE_TIMEOUT: Long = 10_000
sealed class FxComponentPhase(open val log: ArrayList<String> = arrayListOf(),open val errors: ArrayList<Exception> = arrayListOf()) : Phase {
    val defaultTimeout: Long = FX_COMPONENT_PHASE_TIMEOUT
    /**
     * To be called when the component-configuration class is initialized
     */
    data class Launch<N, Data>(
            /**
             * After this time the phase will terminate
             */
            val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
            /**
             * Configuration - launched
             */
            val configuration: Parallel<FxComponentConfiguration<N,Data>>,
            /**
             * Id of the component
             */
            val id: Parallel<ID>,
            /**
             * Stub to be used by component
             */
            val stub: Parallel<Stub<Data>>,
            /**
             * View to be shown
             */
            val view: Parallel<()->N>,
            /**
             * Children defined via child-function in sub-views
             */
            val fxChildren: ArrayList<Parallel<FxComponent<*, *>>>,
            /**
             * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
             * they have to be treated separately by the component
             */
            val fxSpecials: ArrayList<Parallel<FxComponent<*, *>>> = arrayListOf(),
            /**
             * fxRunTime-actions
             */
            val fxRunTime: ArrayList<Parallel<N.()->Unit>> = arrayListOf(),
            /**
             * Actions to be performed on the stub
             */
            val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>> = arrayListOf(),
            /**
             * log
             */
            override val log: ArrayList<String> = arrayListOf()

    ) : FxComponentPhase(log = log)

    /**
     * Collect all data defined before the configure-function is called
     *
     * Within this phase, the id and the stub will be set
     */
    data class PreConfiguration<N, Data>(
            /**
             * After this time the phase will terminate
             */
            val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
            /**
             * Configuration - launched
             */
            val configuration: FxComponentConfiguration<N,Data>,
            /**
             * Id of the component
             */
            val id: Parallel<ID>,
            /**
             * Stub to be used by component
             */
            val stub: Parallel<Stub<Data>>,
            /**
             * View to be shown
             */
            val view: Parallel<()->N>,
            /**
             * Children defined via child-function in sub-views
             */
            val fxChildren: ArrayList<Parallel<FxComponent<*, *>>>,
            /**
             * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
             * they have to be treated separately by the component
             */
            val fxSpecials: ArrayList<Parallel<FxComponent<*, *>>>,
            /**
             * fxRunTime-actions
             */
            val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
            /**
             * Actions to be performed on the stub
             */
            val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>,
            /**
             * log
             */
            override val log: ArrayList<String> = arrayListOf()

    ) : FxComponentPhase(log = log), ConfigurationPhase

    /**
     * Configure the component
     */
    sealed class Configuration(override val log: ArrayList<String>): FxComponentPhase(log = log), ConfigurationPhase {
        /**
         * Collect fxChildren and fxSpecials
         */
        data class Setup<N, Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                /**
                 * Id of the component
                 */
                val id: ID,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<Parallel<FxComponent<*, *>>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component
                 */
                val fxSpecials: ArrayList<Parallel<FxComponent<*, *>>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * Actions to be performed on the stub
                 */
                val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>,
                /**
                 * log
                 */
                override val log: ArrayList<String> = arrayListOf()
        ) : Configuration(log = log)

        /**
         * Add FxChildren and FxStubs as stubs to its parent
         */
        data class AddFxChildren<N,Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                /**
                 * Id of the component
                 */
                val id: ID,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<FxComponent<*, *>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component
                 */
                val fxSpecials: ArrayList<FxComponent<*, *>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * Actions to be performed on the stub
                 */
                val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>,
                /**
                 * log
                 */
                override val log: ArrayList<String> = arrayListOf()
        ) : Configuration(log = log)

        /**
         * Apply the stubActions
         */
        data class ExtendStub<N,Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                /**
                 * Id of the component
                 */
                val id: ID,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<FxComponent<*, *>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component
                 */
                val fxSpecials: ArrayList<FxComponent<*, *>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * Actions to be performed on the stub
                 */
                val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>,
                /**
                 * log
                 */
                override val log: ArrayList<String> = arrayListOf()
        ): Configuration(log = log)

        /**
         * Set id, stub and view of the underlying configuration and call the configure-method within this phase
         */
        data class Configure<N,Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                /**
                 * Id of the component
                 */
                val id: ID,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<FxComponent<*, *>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component
                 */
                val fxSpecials:ArrayList<FxComponent<*, *>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * log
                 */
                override val log: ArrayList<String> = arrayListOf()
        ): Configuration(log = log)
    }

    /**
     * Perform actions like borderPane.top = ...
     */
    data class RunTimeConfiguration<N, D>(
            val timeout: Long = FX_COMPONENT_PHASE_TIMEOUT,
            /**
             * Children defined via child-function in sub-views
             */
            val fxChildren: ArrayList<FxComponent<*, *>>,
            /* TODO think about managing parent-child-relations between fx-handleRequests-times within this phase. This would require the components to know their FxRunTime */
            val configuration: FxComponentConfiguration<N, D>,
            /**
             * fxRunTime-actions
             */
            val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
            /**
             * log
             */
            override val log: ArrayList<String> = arrayListOf()
    ) : FxComponentPhase(log = log), ConfigurationPhase

    sealed class RunTimePhase<N, D>(override val log: ArrayList<String>) : FxComponentPhase(log = log) {


        /**
         * Execute blocks declared using the fxRunTime-function
         */
        data class RunTime<N, D>(val fxRunTime: FxRunTime<N, D>, override val log: ArrayList<String>) : RunTimePhase<N, D>(log = log)
        class ShutDown<N, D>(override val log: ArrayList<String>) : RunTimePhase<N, D>(log = log)
    }

    sealed class TerminationPhase(override val log: ArrayList<String>) : FxComponentPhase(log = log) {
        class TerminateWithErrors(override val errors: ArrayList<Exception>, override val log: ArrayList<String>) : FxComponentPhase(errors = errors, log = log)
        class Terminate(override val log: ArrayList<String>) : FxComponentPhase(log = log)
    }
}