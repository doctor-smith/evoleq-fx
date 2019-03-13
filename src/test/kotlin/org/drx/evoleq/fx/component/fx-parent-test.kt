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

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.hasModifiableChildren
import org.drx.evoleq.fx.dsl.isFxParent
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit

class FxParentTest {
    @Before fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }
    @Test fun isFxParent() {
        val g = Group()
        assert(g.hasModifiableChildren())
        assert(g.isFxParent())
        println(g::class.java.getMethod("getChildren").returnType)

        val b = Button()

        assert(!b.isFxParent())


    }

    @Test fun overrideProtectedToPublic() {
        open class C{
            protected open fun f(): Int = 1
        }
        class D : C() {
            public override fun f(): Int {
                return super.f()
            }
        }
        val d = D()
        println(d.f())
    }
}