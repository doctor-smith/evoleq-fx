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
package org.drx.evoleq.fx.component

import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.dsl.EvoleqDsl
import org.drx.evoleq.fx.dsl.EvoleqFxDsl
import org.drx.evoleq.stub.Stub

interface FxComponent<N, D> : Stub<D> {
    @EvoleqFxDsl
    fun show(): N
    @EvoleqFxDsl
    fun stop()
}

interface FxNoStubComponent<N, D> : FxComponent<N, D>

interface FxTunnelComponent<N, D> : FxComponent<N, D>

abstract class FxInputComponent<I, N, D>(private val inputReceiver: BaseReceiver<I>) : FxComponent<N,D> {

    @EvoleqDsl
    suspend fun input(input: I) {
        inputReceiver.send(input)
    }


    @EvoleqDsl
    suspend fun closeInput() {
        try{inputReceiver.actor.close()}catch(ignored: Exception){
            println("Closing inputReceiver.actor: Error")
            ignored.stackTrace
        }
        try{inputReceiver.channel.close()}catch(ignored: Exception){
            println("Closing inputReceiver.channel: Error")
            ignored.stackTrace
        }
    }
}

@Suppress("unchecked_cast")
@EvoleqDsl
fun <I, N, D> FxComponent<N, D>.withInput(): FxInputComponent<I, N, D> = this as FxInputComponent<I, N, D>

//@Suppress("unchecked_cast")
//fun <I> FxComponent<*, *>.withInput(): FxInputComponent<I, *, *> = this as FxInputComponent<I, *, *>

@Suppress("unchecked_cast")
@EvoleqDsl
fun <N, D> Stub<*>.asFxComponent() : FxComponent<N, D> = this as FxComponent<N, D>

@Suppress("unchecked_cast")
@EvoleqDsl
fun <I> Stub<*>.withInput(): FxInputComponent<I, *, *> = this as FxInputComponent<I, *, *>