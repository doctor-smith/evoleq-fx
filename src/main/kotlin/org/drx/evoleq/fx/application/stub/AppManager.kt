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
import javafx.beans.property.SimpleBooleanProperty
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.drx.evoleq.coroutines.blockUntil
import org.drx.evoleq.coroutines.onNext
import org.drx.evoleq.dsl.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.flow.SuspendedFlow
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.component.FxInputComponent
import org.drx.evoleq.fx.dsl.EvoleqFxDsl
import org.drx.evoleq.fx.dsl.parallelFx
import org.drx.evoleq.fx.stub.FxInputPhase
import org.drx.evoleq.stub.ID
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.drx.evoleq.util.*
import kotlin.reflect.KClass

/**
 * TODO test
 */
abstract class AppManager <Input,Data> : Application(), Stub<AppMessage<Data>> {
    /**
     * Companion
     */
    companion object Manager{
        val TOOLKIT_INITIALIZED = SimpleBooleanProperty( false )
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
        TOOLKIT_INITIALIZED.value = true
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
     * Called in the start method of JavaFX-Application
     */
    @EvoleqFxDsl
    open fun onFxStart(){}

    /**
     * Called in the init function of this class
     */
    @EvoleqFxDsl
    open fun onFxInit(){}

    /**
     * Called in the stop method of JavaFX-Application
     */
    @EvoleqFxDsl
    open fun onFxStop(){}

    /******************************************************************************************************************
     *
     * Stub - Application Management
     *
     * Define the stub managing the application
     *
     ******************************************************************************************************************/

    /**
     * Id of the AppManager
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
                blockUntil(TOOLKIT_INITIALIZED){ tI -> tI == true }
                message.stages.forEach {
                    entry -> registry[entry.first] = entry.second  as suspend () -> FxComponent<Stage, Data>
                }
                AppMessage.Response.StagesRegistered<Data>(message.data)
            }
            is AppMessage.Request.ShowStage<*> -> scope.parallel {
                val stub = showStage(message.id, message.processId).get()
                if(message.processId != null) {
                    stubs[message.processId] = stub
                } else {
                    stubs[message.id] = stub
                }
                AppMessage.Response.StageShown(stub, message.processId, message.data)
            }
            is AppMessage.Request.HideStage -> scope.parallel{
                if(message.processId != null) {
                    stubs.remove(message.processId)
                } else {
                    stubs.remove(message.id)
                }
                hideStage(message.id, message.processId).get()
                AppMessage.Response.StageHidden<Data>(message.id, message.processId, message.data)
            }
        }
        is AppMessage.Response<*> -> when(message) {
            is AppMessage.Response.StageShown -> scope.parallel{
                onStageShown(message.stub.id, message.processId, message)
            }
            is AppMessage.Response.StageHidden -> scope.parallel {
                onStageHidden(message.id, message.processId, message)
            }
            is AppMessage.Response.StagesRegistered -> scope.parallel{
                onStagesRegistered(message)
            }
        }
        is AppMessage.Process<*> -> when(message){
            is AppMessage.Process.Start<*> ->  scope.parallel{
                AppMessage.Request.RegisterStages(stages(), message.data)
            }
            is AppMessage.Process.DriveStub<*> -> scope.parallel{
                require(message is AppMessage.Process.DriveStub<Data>)
                onDriveStub(message.stub, message.processId, message.data)
            }
            is AppMessage.Process.Wait<*> -> {
                blockUntil(inputStack.isEmpty and updateStack.isEmpty){value -> !value}
                if(updateStack.isNotEmpty()){
                    updateStack.onNext { update ->
                        scope.parallel {
                            AppMessage.Process.Wait(
                                with(update.update(message.data)){
                                    val (senderId,data) = this
                                    onUpdate(senderId, data)
                                }
                            )
                        }
                    }
                } else {
                    inputStack.onNext { input ->
                        onInput(input, message.data)
                    }
                }
            }
            is AppMessage.Process.Error<*> -> scope.parallel {
                onError(message as AppMessage.Process.Error<Data>)
            }
            is AppMessage.Process.Terminate<*> -> scope.parallel {
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
            updateCondition { message ->
                if(message is AppMessage.Process.Terminated){
                    try{inputReceiver.actor.close()}catch(ignored: Exception){
                        println("Closing inputReceiver.actor of AppManager: Error")
                        ignored.stackTrace
                    }
                    try{inputReceiver.channel.close()}catch(ignored: Exception){
                        println("Closing inputReceiver.channel of AppManager: Error")
                        ignored.stackTrace
                    }
                }
                message !is AppMessage.Process.Terminated<*>
            }
        }
    ) }

    /******************************************************************************************************************
     *
     * Abstract Process API
     *
     ******************************************************************************************************************/
    @EvoleqDsl
    abstract fun initData(): Data

    /**
     * Define stages to be registered
     */
    @EvoleqDsl
    abstract suspend fun stages(): ArrayList<Pair<ID,suspend ()->FxComponent<Stage,Data>>>

    /**
     * When stages are registered perform this action
     */
    @EvoleqDsl
    abstract suspend fun onStagesRegistered(data: AppMessage<Data>): AppMessage<Data>

    /**
     * When a stage is shown, perform this action
     */
    @EvoleqDsl
    abstract suspend fun onStageShown(id: ID, processId: ID?, data: AppMessage.Response.StageShown<Data>): AppMessage<Data>

    /**
     * When a stage is closed / hidden, perform this action
     */
    @EvoleqDsl
    abstract suspend fun onStageHidden(id: ID, processId: ID?, data: AppMessage<Data>): AppMessage<Data>

    /**
     * When a new stub arises
     */
    @EvoleqDsl
    abstract suspend fun onDriveStub(stub: Stub<Data>, processId: ID?, initialData: Data): AppMessage<Data>

    /**
     *
     */
    @EvoleqDsl
    abstract suspend fun onError(error: AppMessage.Process.Error<Data>): AppMessage<Data>

    /******************************************************************************************************************
     *
     * Private JavaFX API
     *
     ******************************************************************************************************************/

    /**
     * Registry for fx-stages
     */
    private val registry: HashMap<ID,suspend ()->FxComponent<Stage, Data>> = hashMapOf()

    /**
     * Registry of running stages
     */
    private val stages: HashMap<ID, Stage> = hashMapOf()
    /**
     * Show a registered stage
     */
    @EvoleqFxDsl
    private fun showStage(id: ID, processId: ID?): Evolving<Stub<Data>> = scope.parallel {

        val stubPicker = registry[id]!!

        val stub: FxComponent<Stage,Data> = stubPicker()

        parallelFx{
            val stage = stub.show()
            if(processId != null) {
                stages[processId] = stage
            } else {
                stages[id] = stage
            }
            showStage(stage)
        }
        stub
    }

    /**
     * Show stage
     */
    @EvoleqFxDsl
    private fun showStage(stage: Stage): Evolving<Unit> = scope.parallel{
        parallelFx{
            stage.show()
        }
        Unit
    }

    /**
     * Hide stage
     */
    @EvoleqFxDsl
    private fun hideStage(id: ID, processId: ID?): Evolving<ID> = scope.parallel{
        try{
            parallelFx {
                val stage = stages[processId ?: id]!!
                stage.close()
                stages.remove(processId ?: id)
            }.get()
            id
        } catch(exception : Exception) {
            processId ?: id
        }
    }

    /******************************************************************************************************************
     *
     * Input
     *
     ******************************************************************************************************************/

    /**
     * InputReceiver
     */
    private val inputReceiver = CoroutineScope(Job()).receiver<Input>(capacity = 10_000) {  }

    /**
     * InputStack
     */
    private val  inputStack = smartArrayListOf<Input>()

    /**
     * Called in the waiting-phase ([AppMessage.Process.Wait]) of the application flow
     */
    @EvoleqDsl
    abstract  fun  onInput(input: Input, data:  Data): Evolving<AppMessage<Data>>

    /**
     * Input function. Use this function to pass input to the appöication (-flow)
     */
    @Suppress("unused")
    @EvoleqDsl
    suspend fun input(input: Input) = inputReceiver.send(input)

    /******************************************************************************************************************
     *
     * Outputs
     *
     ******************************************************************************************************************/

    /**
     * Outputs:
     */
    private val outputs: HashMap<ID, (Nothing)->Evolving<Unit>> by lazy { hashMapOf<ID, (Nothing)->Evolving<Unit>>()}

    /**
     * Register an output-function
     */
    @Suppress("unused")
    @EvoleqDsl
    fun outputs(put: ()->Pair<ID, out (Nothing)->Evolving<Unit>>) {
        val pair = put()
        outputs[pair.first] = pair.second
    }

    /**
     * Retrieve an output function
     * Hint: Throws an exception if the desired output does not exist
     */
    @Suppress("unused")
    @EvoleqDsl
    fun outputs(id: ID): (Nothing)->Evolving<Unit> = outputs[id]!!

    /**
     * Remove an output function
     */
    @Suppress("unused")
    @EvoleqDsl
    fun removeOutput(id: ID) {
        outputs.remove(id)
    }

    /******************************************************************************************************************
     *
     * Update
     *
     ******************************************************************************************************************/
    /**
     * InputReceiver
     */
    private val updateReceiver = CoroutineScope(Job()).receiver<Update<ID,Data>>(capacity = 10_000) {  }
    
    /**
     * InputStack
     */
    private val  updateStack = smartArrayListOf<Update<ID,Data>>()
    /**
     * Update an child component provided that it is an input-component
     */
    @Suppress("unchecked_cast")
    @EvoleqDsl
    suspend fun <E> updateComponent(componentId: ID, update: suspend E.()->E) = try {
        (stubs[componentId]!! as FxInputComponent<*, *, E>).update(id) { update() }
    } catch(ex:Exception){
        throw ex
    }
    
    /**
     * Update the app-manager
     */
    @EvoleqDsl
    suspend fun update(sender: ID, update: suspend Data.()->Data) {
        updateReceiver.send(Update(sender){ update(this) })
    }
    
    /**
     * OnUpdate
     */
    @EvoleqDsl
    abstract suspend fun onUpdate(senderId: ID, data: Data): Data


    /******************************************************************************************************************
     *
     * Processes API
     *
     *****************************************************************************************************************/

    /**
     * Processes:
     */
    private val processes: HashMap<ID, Evolving<Any>> by lazy { hashMapOf<ID, Evolving<Any>>() }

    /**
     * Register a process
     */
    @Suppress("unused")
    @EvoleqDsl
    suspend fun processes(put: suspend HashMap<ID, Evolving<Any>>.()->Pair<ID, Evolving<Any>>): Unit {
        //scope.parallel {
            val pair = processes.put()
            processes[pair.first] = pair.second as Evolving<Any>
        //}.get()
    }

    /**
     * Get a process
     */
    @Suppress("unused")
    @EvoleqDsl
    fun processes(id: ID) : Evolving<Any> = processes[id]!!

    /**
     * Remove a process
     */
    @Suppress("unused")
    @EvoleqDsl
    fun removeProcess(id: ID) = processes.remove(id)

    /**
     * Get processes
     */
    @Suppress("unused")
    @EvoleqDsl
    fun processes() = processes

    /******************************************************************************************************************
     *
     * Initialization
     *
     ******************************************************************************************************************/
    init{
        inputReceiver.onNext(scope){input -> inputStack.add(input)}
        updateReceiver.onNext(scope){update -> updateStack.add(update)}
    }
}