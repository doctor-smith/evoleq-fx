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
package org.drx.evoleq.fx.application

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.Keys
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import kotlin.reflect.KClass

class PreId

val numberOfKeys: Int by lazy{ Keys.size }
fun CoroutineScope.idProvider() = actor<SimpleObjectProperty<ID>>(){
    val currentId = SimpleIntegerProperty(0)
    for(property in channel) {
        val id = currentId.value
        if (id == numberOfKeys - 1) {
            currentId.value = 0
        } else {
            currentId.value = id + 1
        }

        property.value = Keys[id]
    }
}