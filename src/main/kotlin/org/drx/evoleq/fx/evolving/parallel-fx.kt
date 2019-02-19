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
package org.drx.evoleq.fx.evolving

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import kotlinx.coroutines.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel

class ParallelFx<D>(
        private val delay: Long = 1,
        val scope: CoroutineScope = GlobalScope,
        private val block:  ParallelFx<D>.() -> D
) : Evolving<D> {

    private var deferred: Deferred<D>? = null

    init{
        scope.launch{ coroutineScope{
            deferred = async {
                var d:D? = null
                Platform.runLater {
                    d = this@ParallelFx.block()
                }
                while(d == null){
                    delay(1)
                }
                d!!
            }
        }}
    }

    override suspend fun get(): D {
        while(deferred == null) {
            delay(delay)
        }
        return deferred!!.await()
    }

    fun cancel(d: D): Evolving<D> = Immediate {
        while(deferred == null) {
            delay(delay)
        }
        deferred!!.cancel()
        d
    }

    fun job(): Job = deferred!!
}
