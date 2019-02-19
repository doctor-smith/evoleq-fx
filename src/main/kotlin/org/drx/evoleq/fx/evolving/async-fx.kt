package org.drx.evoleq.fx.evolving

import javafx.application.Platform
import kotlinx.coroutines.*
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate


class AsyncFx<D>(
        private val delay: Long = 1,
        val scope: CoroutineScope = GlobalScope,
        private val block:  AsyncFx<D>.() -> D
) : Evolving<D> {

    private var deferred: Deferred<D>? = null

    init {
        scope.launch { coroutineScope {
            deferred = async {
                var d:D? = null
                Platform.runLater {
                    d = this@AsyncFx.block()
                }
                while(d == null){
                    delay(1)
                }
                d!!
            }
        }}
    }

    override suspend fun get(): D {
        while (deferred == null) {
            delay(delay)
        }
        return deferred!!.await()
    }

    fun cancel(d: D): Evolving<D> = Immediate {
        while (deferred == null) {
            delay(delay)
        }
        deferred!!.cancel()
        d
    }

    fun job(): Job = deferred!!
}