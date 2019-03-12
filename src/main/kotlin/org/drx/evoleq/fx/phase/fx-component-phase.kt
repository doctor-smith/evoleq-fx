package org.drx.evoleq.fx.phase

import org.drx.evoleq.dsl.ConfigurationPhase
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.flow.Phase
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponentConfiguration
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass
const val  fxComponentPhaseTimeout: Long = 1_000
sealed class FxComponentPhase : Phase {
    val defaultTimeout: Long = 100
    /**
     * To be called when the component-configuration class is initialized
     */
    data class Launch<N, Data>(
            /**
             * After this time the phase will terminate
             */
            val timeout: Long = fxComponentPhaseTimeout,
            /**
             * Configuration - launched
             */
            val configuration: Parallel<FxComponentConfiguration<N,Data>>,
            // common to all fx-components
            /**
             * Id of the component
             */
            val id: Parallel<KClass<*>>,
            /**
             * Stub to be used by component
             */
            val stub: Parallel<Stub<Data>>,
            /**
             * View to be shown
             */
            val view: Parallel<()->N>,
            // needed for components implementing FxParentComponent
            /**
             * Children defined via child-function in sub-views
             */
            val fxChildren: ArrayList<Parallel<FxComponent<*,*>>>,
            /**
             * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
             * they have to be treated separately by the component, e.g in the show-function
             */
            val fxSpecials: ArrayList<Parallel<FxComponent<*,*>>> = arrayListOf(),
            /**
             * fxRunTime-actions
             */
            val fxRunTime: ArrayList<Parallel<N.()->Unit>> = arrayListOf(),
            /**
             * Actions to be performed on the stub
             */
            val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>> = arrayListOf()

    ) : FxComponentPhase()

    /**
     * Collect all data defined before the configure-function is called
     */
    data class PreConfiguration<N, Data>(
            /**
             * After this time the phase will terminate
             */
            val timeout: Long = fxComponentPhaseTimeout,
            /**
             * Configuration - launched
             */
            val configuration: FxComponentConfiguration<N,Data>,
            // common to all fx-components
            /**
             * Id of the component
             */
            val id: Parallel<KClass<*>>,
            /**
             * Stub to be used by component
             */
            val stub: Parallel<Stub<Data>>,
            /**
             * View to be shown
             */
            val view: Parallel<()->N>,
            // needed for components implementing FxParentComponent
            /**
             * Children defined via child-function in sub-views
             */
            val fxChildren: ArrayList<Parallel<FxComponent<*,*>>>,
            /**
             * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
             * they have to be treated separately by the component, e.g in the show-function
             */
            val fxSpecials: ArrayList<Parallel<FxComponent<*,*>>>,
            /**
             * fxRunTime-actions
             */
            val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
            /**
             * Actions to be performed on the stub
             */
            val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>

    ) : FxComponentPhase(), ConfigurationPhase

    sealed class Configuration: FxComponentPhase(), ConfigurationPhase {
        data class Setup<N, Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = fxComponentPhaseTimeout,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                // common to all fx-components
                /**
                 * Id of the component
                 */
                val id: KClass<*>,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                // needed for components implementing FxParentComponent
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<Parallel<FxComponent<*,*>>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component, e.g in the show-function
                 */
                val fxSpecials: ArrayList<Parallel<FxComponent<*,*>>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * Actions to be performed on the stub
                 */
                val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>
        ) : Configuration()
        data class AddFxChildren<N,Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = fxComponentPhaseTimeout,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                // common to all fx-components
                /**
                 * Id of the component
                 */
                val id: KClass<*>,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                // needed for components implementing FxParentComponent
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<FxComponent<*,*>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component, e.g in the show-function
                 */
                val fxSpecials: ArrayList<FxComponent<*,*>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * Actions to be performed on the stub
                 */
                val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>
        ) : Configuration()
        data class ExtendStub<N,Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = fxComponentPhaseTimeout,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                // common to all fx-components
                /**
                 * Id of the component
                 */
                val id: KClass<*>,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                // needed for components implementing FxParentComponent
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<FxComponent<*,*>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component, e.g in the show-function
                 */
                val fxSpecials: ArrayList<FxComponent<*,*>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>,
                /**
                 * Actions to be performed on the stub
                 */
                val stubActions: ArrayList<Parallel<Stub<Data>.()->Unit>>
        ): Configuration()
        data class Configure<N,Data>(
                /**
                 * After this time the phase will terminate
                 */
                val timeout: Long = fxComponentPhaseTimeout,
                /**
                 * Configuration - launched
                 */
                val configuration: FxComponentConfiguration<N,Data>,
                // common to all fx-components
                /**
                 * Id of the component
                 */
                val id: KClass<*>,
                /**
                 * Stub to be used by component
                 */
                val stub: Stub<Data>,
                /**
                 * View to be shown
                 */
                val view: Parallel<()->N>,
                // needed for components implementing FxParentComponent
                /**
                 * Children defined via child-function in sub-views
                 */
                val fxChildren: ArrayList<FxComponent<*,*>>,
                /**
                 * Think of top, bottom, etc of BorderPane. The 'result' of these of must not be added to fxChildren,
                 * they have to be treated separately by the component, e.g in the show-function
                 */
                val fxSpecials:ArrayList<FxComponent<*,*>>,
                /**
                 * fxRunTime-actions
                 */
                val fxRunTime: ArrayList<Parallel<N.()->Unit>>
        ): Configuration()
    }

    /**
     * Perform actions like borderPane.top = ...
     */
    data class RunTimeConfiguration<N, D>(
            val timeout: Long = fxComponentPhaseTimeout,
            /**
             * Children defined via child-function in sub-views
             */
            val fxChildren: ArrayList<FxComponent<*,*>>,
            val configuration: FxComponentConfiguration<N, D>
    ) : FxComponentPhase(), ConfigurationPhase

    sealed class RunTimePhase<N, D> : FxComponentPhase() {
        /**
         * Execute blocks declared using the fxRunTime-function
         */
        data class RunTime<N, D>(val fxRunTime: FxRunTime<N, D>) : RunTimePhase<N, D>()
        class ShutDown<N, D> : RunTimePhase<N, D>()
    }


    class TerminateWitErrors(val errors: ArrayList<Exception>) : FxComponentPhase()
    class Terminate : FxComponentPhase()
}