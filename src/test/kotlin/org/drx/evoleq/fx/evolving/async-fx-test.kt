package org.drx.evoleq.fx.evolving

import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.testfx.api.FxToolkit
import java.lang.Thread.sleep

class AsyncFxTest {
    val primaryStage: Stage = FxToolkit.registerPrimaryStage()
    @Test fun runsOnApplicationThread() = runBlocking{
        val asyncFx = AsyncFx<String> {
            Thread.currentThread().name
        }
        val threadName = asyncFx.get()

        assert(threadName == "JavaFX Application Thread")
    }
}