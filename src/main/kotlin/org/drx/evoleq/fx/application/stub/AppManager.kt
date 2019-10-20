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
package org.drx.evoleq.fx.application.stub

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.flow.SuspendedFlow
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.parallelFx
import org.drx.evoleq.stub.ID
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import kotlin.reflect.KClass

/**
 * TODO test
 */
abstract class AppManager <Data> : Application(), Stub<AppMessage<Data>> {
    /**
     * Companion
     */
    companion object Manager{
        var TOOLKIT_INITIALIZED: Boolean = false
    }

    /******************************************************************************************************************
     *
     * JavaFX-Application stuff
     *
     ******************************************************************************************************************/

    /**
     * Do not override!
     * Use function onFxInit instead!
     *
     * Launches the application flow and calls onFxInit
     */
    override fun init() {

        scope.parallel {
            val phase = flow.evolve(AppMessage.Process.Start( initData() )).get()
            assert(phase is AppMessage.Process.Terminated)
        }

        onFxInit()
    }

    /**
     * Do not override!
     * Use function onFxStart instead
     *
     * Calls onFxInit
     */
    override fun start(primaryStage: Stage?) {
        TOOLKIT_INITIALIZED = true
        onFxStart()
    }

    /**
     * Do not override!
     * Use function onFxStop instead
     *
     * Calls onFxStop
     */
    override fun stop() {
        onFxStop()
    }

    /******************************************************************************************************************
     *
     * Open JavaFX Process API
     *
     ******************************************************************************************************************/

    /**
     *
     */
    open fun onFxStart(){}

    /**
     *
     */
    open fun onFxInit(){}

    /**
     *
     */
    open fun onFxStop(){}

    /******************************************************************************************************************
     *
     * Stub - Application Management
     *
     * Define the stub managing the application
     *
     ******************************************************************************************************************/

    /**
     * Id
     */
    override val id: KClass<*>
        get() = AppManager::class

    /**
     * Scope of the application
     */
    override val scope: CoroutineScope = CoroutineScope(Job())

    /**
     * Stubs
     */
    override val stubs: HashMap<KClass<*>, Stub<*>> by lazy { hashMapOf<KClass<*>, Stub<*>>() }
    /**
     * Evolve data along the application-flow
     */
    @Suppress("unchecked_cast")
    override suspend fun evolve(d: AppMessage<Data>): Evolving<AppMessage<Data>> = when(val message = d){
        is AppMessage.Request<*> -> when(message) {
            is AppMessage.Request.RegisterStages<*> -> scope.parallel {
                // println("register")
                while(!TOOLKIT_INITIALIZED) {
                    delay(1)
                }
                message.stages.forEach { entry -> registry[entry.first] = entry.second  as () -> FxComponent<Stage, Data> }
                AppMessage.Response.StagesRegistered<Data>(message.data)
            }
            is AppMessage.Request.ShowStage<*> -> scope.parallel {
                // println("show")
                val stub = showStage(message.id).get()
                AppMessage.Response.StageShown(stub, message.data)
            }
            is AppMessage.Request.HideStage -> scope.parallel{
                // println("hide")
                hideStage(message.id).get()
                AppMessage.Response.StageHidden<Data>(message.id, message.data)
            }

        }
        is AppMessage.Response<*> -> when(message) {
            is AppMessage.Response.StageShown -> scope.parallel{
                // println("shown")
                onStageShown(message.stub.id, message)
            }
            is AppMessage.Response.StageHidden -> scope.parallel {
                // println("hidden")
                onStageHidden(message.id,message)
            }
            is AppMessage.Response.StagesRegistered -> scope.parallel{
                // println("registered")
                onStagesRegistered(message)
            }

        }
        is AppMessage.Process<*> -> when(message){
            is AppMessage.Process.Start<*> ->  scope.parallel{
                AppMessage.Request.RegisterStages(stages(), message.data)
            }
            is AppMessage.Process.DriveStub<*> -> scope.parallel{
                require(message is AppMessage.Process.DriveStub<Data>)
                onDriveStub(message.stub, message.data)
            }
            is AppMessage.Process.Error<*> -> scope.parallel {
                onError(message as AppMessage.Process.Error<Data>)
            }
            is AppMessage.Process.Terminate<*> -> scope.parallel {
                // println("terminate")
                AppMessage.Process.Terminated<Data>(message.data)
            }
            is AppMessage.Process.Terminated<*> -> scope.parallel { message }
        }
    }

    /******************************************************************************************************************
     *
     * Flows of the application
     *
     ******************************************************************************************************************/

    /**
     * Application flow
     */
    private val flow: SuspendedFlow<AppMessage<Data>, Boolean> by lazy{ toFlow<AppMessage<Data>, Boolean>(
        conditions{
            testObject(true)
            check{ b -> b }
            updateCondition { message -> message !is AppMessage.Process.Terminated<*> }
        }
    ) }

    /******************************************************************************************************************
     *
     * Abstract Process API
     *
     ******************************************************************************************************************/
    abstract fun initData(): Data

    /**
     * Define stages to be registered
     */
    abstract fun stages(): ArrayList<Pair<ID, ()->FxComponent<Stage,Data>>>

    /**
     * When stages are registered perform this action
     */
    abstract suspend fun onStagesRegistered(data: AppMessage<Data>): AppMessage<Data>

    /**
     * When a stage is shown, perform this action
     */
    abstract suspend fun onStageShown(id: ID, data: AppMessage.Response.StageShown<Data>): AppMessage<Data>

    /**
     * When a stage is closed / hidden, perform this action
     */
    abstract suspend fun onStageHidden(id: ID, data: AppMessage<Data>): AppMessage<Data>

    /**
     * When a new stub arises
     */
    abstract suspend fun onDriveStub(stub: Stub<Data>, initialData: Data): AppMessage<Data>

    /**
     *
     */
    abstract suspend fun onError(error: AppMessage.Process.Error<Data>): AppMessage<Data>

    /******************************************************************************************************************
     *
     * Private JavaFX API
     *
     ******************************************************************************************************************/

    /**
     * Registry for fx-stages
     */
    private val registry: HashMap<ID, ()->FxComponent<Stage, Data>> = hashMapOf()

    /**
     * Registry of running stages
     */
    private val stages: HashMap<ID, Stage> = hashMapOf()

    /**
     * Show a registered stage
     */
    private fun showStage(id: ID): Evolving<Stub<Data>> = scope.parallel {

        val stubPicker = registry[id]!!
        //println("get stub")

        val stub: FxComponent<Stage,Data> = stubPicker()

        //println("stub got")
        parallelFx{
            val stage = stub.show()
            stages[id] = stage
            showStage(stage)
        }

        stub
    }

    /**
     * Show stage
     */
    private fun showStage(stage: Stage): Evolving<Unit> = scope.parallel{
        parallelFx{
            stage.show()
        }
        Unit
    }

    /**
     * Hide atege
     */
    private fun hideStage(id: ID): Evolving<ID> = scope.parallel{
        try{
            parallelFx {
                val stage = stages[id]!!
                stage.close()
                stages.remove(id)
            }.get()
            id
        } catch(exception : Exception) {
            // println("Could not hide stage $id: $exception")
            id
        }
    }

    /******************************************************************************************************************
     *
     * IO
     *
     ******************************************************************************************************************/

    //val outputStack: ArrayList<Any> by lazy{ arrayListOf<Any>() }

}