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

import javafx.scene.Scene
import javafx.scene.input.*

/**
 * Scene - MouseEvents
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseReleased(action: MouseEvent.()->Unit) : N {
    this.setOnMouseReleased { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMousePressed(action: MouseEvent.()->Unit) : N {
    this.setOnMousePressed { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseClicked(action: MouseEvent.()->Unit) : N {
    this.setOnMouseClicked { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseDragged(action: MouseEvent.()->Unit) : N {
    this.setOnMouseDragged { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseDragDetected(action: MouseEvent.()->Unit) : N {
    this.setOnDragDetected { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseMoved(action: MouseEvent.()->Unit) : N {
    this.setOnMouseMoved { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseEntered(action: MouseEvent.()->Unit) : N {
    this.setOnMouseEntered { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseExited(action: MouseEvent.()->Unit) : N {
    this.setOnMouseExited{ it.action() }
    return this
}

/**
 * Mouse Drag Events
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseDragEntered(action: MouseDragEvent.()->Unit) : N {
    this.setOnMouseDragEntered { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onMouseDragExited(action: MouseDragEvent.()->Unit) : N {
    this.setOnMouseDragExited{ it.action() }
    return this
}
/**
 * Drag Events
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onDragEntered(action: DragEvent.()->Unit) : N {
    this.setOnDragEntered { it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onDragExited(action: DragEvent.()->Unit) : N {
    this.setOnDragExited{ it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onDragOver(action: DragEvent.()->Unit) : N {
    this.setOnDragOver{ it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onDragDone(action: DragEvent.()->Unit) : N {
    this.setOnDragDone{ it.action() }
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onDragDropped(action: DragEvent.()->Unit) : N {
    this.setOnDragDropped{ it.action() }
    return this
}

/**
 * Rotate
 */

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onRotate(action: RotateEvent.()->Unit) : N {
    this.setOnRotate(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onKRotationStarted(action: RotateEvent.()->Unit) : N {
    this.setOnRotationStarted(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onRotationFinished(action: RotateEvent.()->Unit) : N {
    this.setOnRotationFinished(action)
    return this
}

/**
 * Scroll
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onScroll(action: ScrollEvent.()->Unit) : N {
    this.setOnScroll(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onScrollStarted(action: ScrollEvent.()->Unit) : N {
    this.setOnScrollStarted(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onScrollFinished(action: ScrollEvent.()->Unit) : N {
    this.setOnScrollFinished(action)
    return this
}

/**
 * Swipe
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onSwipeUp(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeUp(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onSwipeDown(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeDown(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onSwipeLeft(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeLeft(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onSwipeRight(action: SwipeEvent.()->Unit) : N {
    this.setOnSwipeRight(action)
    return this
}

/**
 * Touch
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onTouchMoved(action: TouchEvent.()->Unit) : N {
    this.setOnTouchMoved(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onTouchPressed(action: TouchEvent.()->Unit) : N {
    this.setOnTouchPressed(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onTouchReleased(action: TouchEvent.()->Unit) : N {
    this.setOnTouchReleased(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onTouchStationary(action: TouchEvent.()->Unit) : N {
    this.setOnTouchStationary(action)
    return this
}

/**
 * Zoom
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onZoom(action: ZoomEvent.()->Unit) : N {
    this.setOnZoom(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onZoomStarted(action: ZoomEvent.()->Unit) : N {
    this.setOnZoomStarted(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onZoomFinished(action: ZoomEvent.()->Unit) : N {
    this.setOnZoomFinished(action)
    return this
}


/**
 * Key Events
 */
@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onKeyPressed(action: KeyEvent.()->Unit) : N {
    this.setOnKeyPressed(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onKeyTyped(action: KeyEvent.()->Unit) : N {
    this.setOnKeyTyped(action)
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun <N : Scene> N.onKeyReleased(action: KeyEvent.()->Unit) : N {
    this.setOnKeyReleased(action)
    return this
}