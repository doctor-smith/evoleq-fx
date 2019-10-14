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
package org.drx.evoleq.fx.stub

import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.dsl.DefaultStubScope
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

class NoStub<D> : Stub<D> {
    override val scope: CoroutineScope  = DefaultStubScope()

    override val id: KClass<*>
        get() = this::class
    override val stubs: HashMap<KClass<*>, Stub<*>> = HashMap()
}