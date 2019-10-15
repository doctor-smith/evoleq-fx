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

import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.application.configration.AppManager
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass


class LaunchApplicationStubConfiguration<D, A: AppManager<D>> : Configuration<Stub<Stub<D>?>> {

    private lateinit var application: A

    override fun configure(): Stub<Stub<D>?> = stub{
        evolve{ AppManager.launch(application) }
    }

    fun application(application: A){
        this.application = application
    }


}

fun <D, A: AppManager<D>> launchApplicationStub(configuration: LaunchApplicationStubConfiguration<D, A>.()->Unit): Stub<Stub<D>?> = configure(configuration)


class LaunchApplicationStubFromClassConfiguration<D, A: KClass<out AppManager<D>>> : Configuration<Stub<Stub<D>?>> {

    private lateinit var application: A

    override fun configure(): Stub<Stub<D>?> = stub{
        evolve{ AppManager.launch(application) }
    }

    fun application(application: A){
        this.application = application
    }


}
@Suppress("unused")
fun <D, A: KClass<out AppManager<D>>> launchApplicationStubFromClass(configuration: LaunchApplicationStubFromClassConfiguration<D, A>.()->Unit): Stub<Stub<D>?> = configure(configuration)