package org.drx.evoleq.fx.application.deprecated

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.Keys
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import kotlin.reflect.KClass

/**
 * @deprecated
 */
class IdProvider  {
    private val requests: ArrayList<SimpleObjectProperty<ID>> by lazy{ ArrayList<SimpleObjectProperty<ID>>() }
    private val currentId = SimpleIntegerProperty(0)
    private val subStubs: HashMap<ID, Stub<*>> by lazy{ HashMap<ID, Stub<*>>() }

    private var cancel: Boolean = false

    private val numberOfKeys = Keys.size

    val stub = object: Stub<Boolean> {
        override val id: KClass<*>
            get() = this::class
        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = subStubs

        override suspend fun evolve(d: Boolean): Evolving<Boolean> = handleRequests()


        private fun handleRequests() =  Parallel<Boolean>{
            while(requests.isEmpty() && !cancel) {
                delay(1)
            }
            while(requests.isNotEmpty() && !cancel) {
                synchronized(requests) {
                    val id = currentId.value
                    if (id == numberOfKeys - 1) {
                        currentId.value = 0
                    } else {
                        currentId.value = id + 1
                    }
                    val req = requests.first()
                    requests.removeAt(0)
                    req!!
                    req.value = Keys[id]
                }
            }
            !cancel
        }
    }

    val flow = stub.toFlow<Boolean, Boolean>(conditions{
        testObject(true)
        check{b -> b}
        updateCondition { d -> !cancel && d }
    })


    @Synchronized fun add( request: SimpleObjectProperty<ID>)  {
        requests.add(request)
    }

    fun run() = Parallel<Unit>{
        flow.evolve(true).get()
        cancel(Unit)
    }
    fun terminate() = Parallel<Unit> {
        cancel = true
    }
}
