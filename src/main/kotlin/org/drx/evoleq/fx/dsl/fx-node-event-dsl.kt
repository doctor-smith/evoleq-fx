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
import javafx.scene.input.*

/**
 * Node - MouseEvents
 */
fun <N : Node> N.onMouseReleased(action: MouseEvent.()->Unit) : N {
    this.setOnMouseReleased { it.action() }
    return this
}
fun <N : Node> N.onMousePressed(action: MouseEvent.()->Unit) : N {
    this.setOnMousePressed { it.action() }
    return this
}
fun <N : Node> N.onMouseClicked(action: MouseEvent.()->Unit) : N {
    this.setOnMouseClicked { it.action() }
    return this
}
fun <N : Node> N.onMouseDragged(action: MouseEvent.()->Unit) : N {
    this.setOnMouseDragged { it.action() }
    return this
}
fun <N : Node> N.onMouseDragDetected(action: MouseEvent.()->Unit) : N {
    this.setOnDragDetected { it.action() }
    return this
}

fun <N : Node> N.onMouseMoved(action: MouseEvent.()->Unit) : N {
    this.setOnMouseMoved { it.action() }
    return this
}
fun <N : Node> N.onMouseEntered(action: MouseEvent.()->Unit) : N {
    this.setOnMouseEntered { it.action() }
    return this
}
fun <N : Node> N.onMouseExited(action: MouseEvent.()->Unit) : N {
    this.setOnMouseExited{ it.action() }
    return this
}

/**
 * Mouse Drag Events
 */
fun <N : Node> N.onMouseDragEntered(action: MouseDragEvent.()->Unit) : N {
    this.setOnMouseDragEntered { it.action() }
    return this
}

fun <N : Node> N.onMouseDragExited(action: MouseDragEvent.()->Unit) : N {
    this.setOnMouseDragExited{ it.action() }
    return this
}
/**
 * Drag Events
 */
fun <N : Node> N.onDragEntered(action: DragEvent.()->Unit) : N {
    this.setOnDragEntered { it.action() }
    return this
}

fun <N : Node> N.onDragExited(action: DragEvent.()->Unit) : N {
    this.setOnDragExited{ it.action() }
    return this
}

fun <N : Node> N.onDragOver(action: DragEvent.()->Unit) : N {
    this.setOnDragOver{ it.action() }
    return this
}

fun <N : Node> N.onDragDone(action: DragEvent.()->Unit) : N {
    this.setOnDragDone{ it.action() }
    return this
}

fun <N : Node> N.onDragDropped(action: DragEvent.()->Unit) : N {
    this.setOnDragDropped{ it.action() }
    return this
}

/**
 * Rotate
 */
fun <N : Node> N.onRotate(action: RotateEvent.()->Unit) : N {
    this.setOnRotate(action)
    return this
}
fun <N : Node> N.onKRotationStarted(action: RotateEvent.()->Unit) : N {
    this.setOnRotationStarted(action)
    return this
}
fun <N : Node> N.onRotationFinished(action: RotateEvent.()->Unit) : N {
    this.setOnRotationFinished(action)
    return this
}

/**
 * Scroll
 */
fun <N : Node> N.onScroll(action: ScrollEvent.()->Unit) : N {
    this.setOnScroll(action)
    return this
}
fun <N : Node> N.onScrollStarted(action: ScrollEvent.()->Unit) : N {
    this.setOnScrollStarted(action)
    return this
}
fun <N : Node> N.onScrollFinished(action: ScrollEvent.()->Unit) : N {
    this.setOnScrollFinished(action)
    return this
}

/**
 * Swipe
 */
fun <N : Node> N.onSwipeUp(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeUp(action)
    return this
}

fun <N : Node> N.onSwipeDown(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeDown(action)
    return this
}

fun <N : Node> N.onSwipeLeft(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeLeft(action)
    return this
}

fun <N : Node> N.onSwipeRight(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeRight(action)
    return this
}

/**
 * Touch
 */
fun <N : Node> N.onTouchMoved(action: TouchEvent.()->Unit) : N {
    this.setOnTouchMoved(action)
    return this
}

fun <N : Node> N.onTouchPressed(action: TouchEvent.()->Unit) : N {
    this.setOnTouchPressed(action)
    return this
}

fun <N : Node> N.onTouchReleased(action: TouchEvent.()->Unit) : N {
    this.setOnTouchReleased(action)
    return this
}

fun <N : Node> N.onTouchStationary(action: TouchEvent.()->Unit) : N {
    this.setOnTouchStationary(action)
    return this
}

/**
 * Zoom
 */
fun <N : Node> N.onZoom(action: ZoomEvent.()->Unit) : N {
    this.setOnZoom(action)
    return this
}

fun <N : Node> N.onZoomStarted(action: ZoomEvent.()->Unit) : N {
    this.setOnZoomStarted(action)
    return this
}

fun <N : Node> N.onZoomFinished(action: ZoomEvent.()->Unit) : N {
    this.setOnZoomFinished(action)
    return this
}


/**
 * Key Events
 */
fun <N : Node> N.onKeyPressed(action: KeyEvent.()->Unit) : N {
    this.setOnKeyPressed(action)
    return this
}

fun <N : Node> N.onKeyTyped(action: KeyEvent.()->Unit) : N {
    this.setOnKeyTyped(action)
    return this
}

fun <N : Node> N.onKeyReleased(action: KeyEvent.()->Unit) : N {
    this.setOnKeyReleased(action)
    return this
}