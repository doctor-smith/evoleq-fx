package org.drx.evoleq.fx.evolving

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drx.evoleq.evolving.Evolving

class ParallelFx<D>(private val delay: Long = 1, block:  ()-> D) : Evolving<D> {
    private val property: SimpleObjectProperty<D> = SimpleObjectProperty()
    private var updated = false
    init {
        val listener = ChangeListener<D>{_, oV, nV ->
            if (nV != oV) {
                updated = true
            }
        }
        property.addListener( listener )
        GlobalScope.launch {
            coroutineScope {
                //val init = WaitForProperty(FxApp.TOOLKIT_INIT_PROPERTY).toChange().get()
                launch {
                    Platform.runLater {
                        property.value = block()
                        property.removeListener(listener)
                    }
                }
            }
        }
    }
    override suspend fun get(): D {
        while(!updated){
            delay(delay)  // reason why get has to be suspended
        }
        return property.value
    }
}
