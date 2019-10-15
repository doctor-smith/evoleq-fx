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
package org.drx.evoleq.fx.application.multi

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.Keys
import org.drx.evoleq.time.Change


/* TODO improve id-provider-stuff */

class PreId

val numberOfKeys: Int by lazy{ Keys.size }
/**
 * @deprecated
 */
fun CoroutineScope.idProviderDeprecated() = actor<SimpleObjectProperty<ID>>(){
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


@Suppress("obsolete")
fun CoroutineScope.idProvider(): SendChannel<Change<ID>> = actor(capacity = 1_000_000) {
    var currentId = 0
    val number = numberOfKeys
    for (change in channel) {
        val id = currentId
        currentId = if (id == number - 1) {
            0
        } else {
            id + 1
        }
        //println("@id-provider: id = $id")
        change.value = Keys[id]!!
    }
}

val IdProvider: SendChannel<Change<ID>> by lazy {
    GlobalScope.idProvider()
}