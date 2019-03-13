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

import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import org.drx.evoleq.fx.component.FxComponent

/**
 * AnchorPane
 */
fun <D> Any?.fxAnchorPane(configuration: FxComponentConfiguration<AnchorPane, D>.()->Unit): FxComponent<AnchorPane, D> {
    return fxParentComponent(configuration)
}
fun Node.leftAnchor(anchor: Number) {
    AnchorPane.setLeftAnchor(this, anchor.toDouble())
}
fun Node.rightAnchor(anchor: Number) {
    AnchorPane.setRightAnchor(this, anchor.toDouble())
}
fun Node.topAnchor(anchor: Number) {
    AnchorPane.setTopAnchor(this, anchor.toDouble())
}
fun Node.bottomAnchor(anchor: Number) {
    AnchorPane.setBottomAnchor(this, anchor.toDouble())
}