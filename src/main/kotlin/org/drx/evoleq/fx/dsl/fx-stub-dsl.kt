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
package org.drx.evoleq.fx.dsl

import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.fx.stub.FxStub
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

open class FxStubConfiguration<D> : StubConfiguration<D>() {

    lateinit var stubConf: StubConfiguration<D>

    override fun configure(): FxStub<D> {
        val stub = super.configure()

        val fxStub = object: FxStub<D>() {
            override val id: ID
                get() = stub.id
            override val stubs: HashMap<ID, Stub<*>>
                get() = stub.stubs
            override val parentalStubsMap: HashMap<ID, ID>
                get() = this@FxStubConfiguration.parentalStubsMap
            override val crossChildAccessMap: HashMap<ID, ArrayList<ID>>
                get() = this@FxStubConfiguration.crossChildAccessMap
            override val parentalStubs: HashMap<ID, Stub<*>>
                get() = this@FxStubConfiguration.parentalStubs
        }
        return fxStub
    }

}
fun <D> fxStub(configuration: FxStubConfiguration<D>.()->Unit): FxStub<D> = configure(configuration) as FxStub<D>