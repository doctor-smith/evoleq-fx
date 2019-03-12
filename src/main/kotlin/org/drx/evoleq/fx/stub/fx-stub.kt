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

import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

abstract class FxStub<D> : Stub<D> {
    /**
     * Stubs to be called by children
     */
    abstract val parentalStubs: HashMap<KClass<*>, Stub<*>>// by lazy { HashMap<KClass<*>, Stub<*>>() }

    abstract val parentalStubsMap: HashMap<KClass<*>, KClass<*>>// by lazy { HashMap<KClass<*>, KClass<*>>() }
    /**
     * key: 'Key of a child class'
     * val: 'list of stub identifiers accessible to key-stub'
     */
    abstract val crossChildAccessMap: HashMap<KClass<*>, ArrayList<KClass<*>>>// by lazy{ HashMap<KClass<*>, ArrayList<KClass<*>>>() }

}