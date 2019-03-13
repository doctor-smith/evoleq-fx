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
import javafx.scene.input.MouseEvent

/**
 * Node - MouseEvents
 */
fun <N : Node>N.released(action: MouseEvent.()->Unit) : N {
    this.setOnMouseReleased { it.action() }
    return this
}
fun <N : Node>N.pressed(action: MouseEvent.()->Unit) : N {
    this.setOnMousePressed { it.action() }
    return this
}
fun <N : Node>N.clicked(action: MouseEvent.()->Unit) : N {
    this.setOnMouseClicked { it.action() }
    return this
}
fun <N : Node>N.dragged(action: MouseEvent.()->Unit) : N {
    this.setOnMouseDragged { it.action() }
    return this
}
fun <N : Node>N.dragDetected(action: MouseEvent.()->Unit) : N {
    this.setOnDragDetected { it.action() }
    return this
}
fun <N : Node>N.dragEntered(action: MouseEvent.()->Unit) : N {
    this.setOnMouseDragEntered { it.action() }
    return this
}
fun <N : Node>N.moved(action: MouseEvent.()->Unit) : N {
    this.setOnMouseMoved { it.action() }
    return this
}
fun <N : Node>N.entered(action: MouseEvent.()->Unit) : N {
    this.setOnMouseEntered { it.action() }
    return this
}
fun <N : Node>N.exited(action: MouseEvent.()->Unit) : N {
    this.setOnMouseExited{ it.action() }
    return this
}