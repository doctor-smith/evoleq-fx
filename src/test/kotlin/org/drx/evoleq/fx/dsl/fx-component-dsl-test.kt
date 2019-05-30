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
package org.drx.evoleq.fx.dsl

import javafx.scene.Group
import kotlinx.coroutines.delay
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.test.showInTestStage
import org.junit.Test

class FxComponentDslTest {

    @Test fun children()=fxRunTest{// {
        val c = fxComponent<Group,Nothing> {
            noStub()
            view { configure {  } }
            children {
                item(fxStackPane<Int> {
                    noStub()
                    view { configure {  } }
                })
                child(fxStackPane<Int> {
                    noStub()
                    view { configure {  } }
                })
            }
        }
    }

    @Test fun configureWithConstructorData() = fxRunTest{//runBlocking {
        var done1: Boolean = false
        var done2: Boolean = false
        val c = fxGroup<Nothing>{
            noStub()
            view{configure{}}
            child(fxButton {
                noStub()
                view{configure("Hello1"){
                    done1 = true
                }}
            })
            child(fxButton {
                noStub()
                view{configure(constructor("Hello2")){
                    done2 = true
                }}
            })
        }
        val stub = showInTestStage(c).get()
        delay(1_000)
        assert(done1 && done2)
        //delay(10_000)
    }
}
