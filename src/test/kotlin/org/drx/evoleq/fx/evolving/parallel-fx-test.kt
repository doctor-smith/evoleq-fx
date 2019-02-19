package org.drx.evoleq.fx.evolving

import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.testfx.api.FxToolkit
import java.lang.Thread.sleep

class ParallelFxTest {
    val primaryStage: Stage = FxToolkit.registerPrimaryStage()
    @Test fun runsOnApplicationThread() = runBlocking{
        val parallelFx = ParallelFx<String> {
            Thread.currentThread().name
        }
        val threadName = parallelFx.get()

        assert(threadName == "JavaFX Application Thread")
    }
}