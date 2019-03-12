package org.drx.evoleq.fx.stub

import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

class NoStub<D> : Stub<D> {
    override val id: KClass<*>
        get() = this::class
    override val stubs: HashMap<KClass<*>, Stub<*>>
        get() = HashMap()
}