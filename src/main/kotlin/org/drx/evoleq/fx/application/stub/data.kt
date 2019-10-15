package org.drx.evoleq.fx.application.stub

import javafx.stage.Stage
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.stub.ID
import org.drx.evoleq.stub.Stub

sealed class AppMessage<Data> {
    sealed class Request<Data> : AppMessage<Data>() {
        data class ShowStage<Data>(val id: ID) : Request<Data>()
        data class HideStage<Data>(val id: ID): Request<Data>()
        data class RegisterStages<Data>(val stages:  ArrayList<Pair<ID, ()-> FxComponent<Stage, Data>>>) : Request<Data>()
    }
    sealed class Response<Data> : AppMessage<Data>() {
        data class StageShown<Data>(val stub: Stub<Data>): Response<Data>()
        data class StageHidden<Data>(val id: ID): Response<Data>()
        class StagesRegistered<Data> : Response<Data>()
    }
    sealed class Process<Data> : AppMessage<Data>() {
        class Start<Data> : Process<Data>()
        data class DriveStub<Data>(val stub: Stub<Data>, val initialData: Data) : Process<Data>()
        data class Error<Data>(val stageId: ID, val data: Data, val throwable: Throwable): Process<Data>()
        class Terminate<Data> : Process<Data>()
        class Terminated<Data> : Process<Data>()

    }
}
