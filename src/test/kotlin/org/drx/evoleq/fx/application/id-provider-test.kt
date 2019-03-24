package org.drx.evoleq.fx.application

import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.DefaultIdentificationKey
import org.drx.evoleq.time.WaitForProperty
import org.junit.Test
import java.lang.Exception

class IdProviderTest {
    @Test fun test() = runBlocking {
        val ids = arrayListOf<Parallel<ID>>()
        val provider = IdProvider()
        val process = Parallel<Unit> {
            IntRange(1, 1000).forEach {
                //Parallel<Unit> {
                    ids.add(Parallel {
                        val property = SimpleObjectProperty<ID>(PreId::class)
                        provider.add(property)
                        while(property.value == PreId::class) {
                            kotlinx.coroutines.delay(1)
                        }
                        property.value
                    })
                //}
            }
        }
        Parallel<Unit> {
            provider.run()
        }
        delay(1_000)
        //process.get()

        provider.terminate()

        //withTimeout(1_000) {
            assert(ids.size == 1000)
        println(ids.first().get())
        //ids.map{println(it.get())}
        //}
        var count = 0
        ids.forEach { try{withTimeout(10){
            val id = it.get()

            count++
            println(id)
        }}catch(exception : Exception){}
        }
        println(count)
        delay(1_000)
    }
}