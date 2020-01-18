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

import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent

@Suppress("unused")
@EvoleqFxDsl
fun <W:  Window> W.onCloseRequest(action: WindowEvent.()->Unit): W {
    this.setOnCloseRequest(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <W:  Window> W.onShown(action: WindowEvent.()->Unit): W {
    this.setOnShown(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <W:  Window> W.onShowing(action: WindowEvent.()->Unit): W {
    this.setOnShowing(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <W:  Window> W.onHiding(action: WindowEvent.()->Unit): W {
    this.setOnHiding(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <W:  Window> W.onHidden(action: WindowEvent.()->Unit): W {
    this.setOnHidden(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <S: Stage> S.onCloseRequest(action: WindowEvent.()->Unit): S {
    this.setOnCloseRequest(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <S:  Stage> S.onShown(action: WindowEvent.()->Unit): S {
    this.setOnShown(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <S:  Stage> S.onShowing(action: WindowEvent.()->Unit): S {
    this.setOnShowing(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <S:  Stage> S.onHiding(action: WindowEvent.()->Unit): S {
    this.setOnHiding(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <S:  Stage> S.onHidden(action: WindowEvent.()->Unit): S {
    this.setOnHidden(action)
    return this
}