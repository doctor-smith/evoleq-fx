package org.drx.evoleq.fx.stub

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.drx.evoleq.coroutines.onNext
import org.drx.evoleq.coroutines.suspended
import org.drx.evoleq.dsl.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.math.map
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import kotlin.reflect.KClass


class Input<out I>(val value: I)


private class FxInputStub
sealed class FxInputPhase<D>(open val data: D) {
    data class Start<D>(override val data: D) : FxInputPhase<D>(data)
    data class Wait<D>(override val data: D) : FxInputPhase<D>(data)
    data class Stop<D>(override val data: D) : FxInputPhase< D>(data)
}
class InputStub<I, D>(private val onInput: (I, D)-> Evolving<FxInputPhase<D>>) : Stub<D> {
    override val scope: CoroutineScope by lazy{ DefaultStubScope() }

    override val id: KClass<*>
        get() = this::class
    override val stubs: HashMap<KClass<*>, Stub<*>> = HashMap()

    override suspend fun evolve(d: D): Evolving<D> = innerFlow.evolve(FxInputPhase.Start(d)).map(suspended{ phase: FxInputPhase<D> -> phase.data })

    val inputReceiver = CoroutineScope(Job()).receiver<I> {  }
    private val inputStack = arrayListOf<I>()

    init{
        inputReceiver.onNext(scope){input -> inputStack.add(input)}
    }

    private val innerStub = stub<FxInputPhase<D>>{
        id<FxInputStub>()
        evolve{ phase -> when(phase) {
            is FxInputPhase.Start -> scope.parallel {
                /* TODO insert onStart(data) ? */
                FxInputPhase.Wait<D>(phase.data)
            }
            is FxInputPhase.Wait -> {
                inputStack.onNext{ input ->
                    onInput(input, phase.data)
                }
            }
            is FxInputPhase.Stop -> scope.parallel{phase}
        } }
    }
    private val innerFlow = innerStub.toFlow<FxInputPhase<D>, Boolean>(
            conditions{
                testObject(true)
                check{b -> b}
                updateCondition { phase -> phase !is FxInputPhase.Stop }
            }
    )
}