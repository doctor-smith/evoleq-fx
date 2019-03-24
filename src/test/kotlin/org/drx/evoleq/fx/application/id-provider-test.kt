package org.drx.evoleq.fx.application

import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.time.WaitForProperty
import org.junit.Test

class IdProviderTest {
    @Test fun test() = runBlocking {
        val ids = arrayListOf<Parallel<ID>>()
        val provider = IdProvider()
        val process = Parallel<Unit> {
            IntRange(1, 100).forEach {
                Parallel<Unit> {
                    ids.add(Parallel {
                        val property = SimpleObjectProperty<ID>()
                        provider.add(property)
                        WaitForProperty(property).toChange().get()
                    })
                }
            }
        }
        Parallel<Unit> {
            provider.run()
        }
        delay(1_000)
        provider.terminate()
        /*
        withTimeout(1_000) {
            assert(ids.map { it.get() }.size == 1000)
        }
        */
    println(ids.size)

        delay(1_000)
    }
}