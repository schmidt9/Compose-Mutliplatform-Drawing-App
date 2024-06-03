package ui.screen

import DrawMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.smarttoolfactory.composedrawingapp.ui.theme.backgroundColor
import gesture.PointerEvent
import gesture.pointerEvents
import model.PathProperties
import ui.graphics.PolygonShape
import ui.graphics.Shape
import ui.menu.DrawingPropertiesMenu
import ui.menu.HomeScreenTopMenu
import ui.menu.MenuAction
import ui.menu.PolygonActionsMenu
import ui.menu.ShapesMenu
import viewmodel.HomeScreenModel

class HomeScreen : Screen {
    @Composable
    override fun Content() {

        val screenModel = rememberScreenModel { HomeScreenModel() }

        Scaffold(topBar = {
            TopAppBar(
                elevation = 2.dp,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                title = {},
                actions = {
                    HomeScreenTopMenu(
                        onUndo = {
                            screenModel.performUndo()
                        },
                        onRedo = {
                            screenModel.performRedo()
                        })
                }
            )
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {

                val drawModifier = Modifier
                    .padding(8.dp)
                    .shadow(1.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
                    .pointerEvents(
                        onTap = {
                            screenModel.pointerEvent = PointerEvent.Tap

                            screenModel.currentPosition = it
                            screenModel.previousPosition = it

                            screenModel.clearSelection()
                        },
                        onDoubleTap = {
                            // TODO: impl
                        },
                        onDragStart = {
                            screenModel.pointerEvent = PointerEvent.DragStart

                            screenModel.currentPosition = it
                            screenModel.previousPosition = it

                            screenModel.drawMode = if (screenModel.isSelectionAction) {
                                if (screenModel.selectedShapes.isEmpty()) DrawMode.Draw else DrawMode.MoveSelection
                            } else {
                                DrawMode.Draw
                            }

                            when (screenModel.currentMenuButtonAction) {
                                MenuAction.DrawPolygon -> {
                                    if (screenModel.currentShape.isEmpty) {
                                        screenModel.currentShape = PolygonShape(screenModel.currentPosition)
                                    } else {
                                        screenModel.currentShape.addPoint(screenModel.currentPosition)
                                    }

                                    screenModel.polygonMenuVisible = true
                                }

                                else -> Unit
                            }

                            screenModel.shapesMenuVisible = false
                        },
                        onDrag = { position, dragAmount ->
                            screenModel.pointerEvent = PointerEvent.Drag
                            screenModel.currentPosition = position

                            if (screenModel.isMoveSelectionDrawMode) {
                                screenModel.translateSelectedShapes(dragAmount)
                            }
                        },
                        onDragEnd = {
                            screenModel.pointerEvent = PointerEvent.DragEnd
                        }
                    )

                Canvas(modifier = drawModifier) {

                    when (screenModel.pointerEvent) {
                        PointerEvent.Tap -> {
                            when (screenModel.currentMenuButtonAction) {
                                MenuAction.DrawPolygon -> {
                                    if (screenModel.currentShape.isEmpty) {
                                        screenModel.currentShape = PolygonShape(screenModel.currentPosition)
                                    } else {
                                        screenModel.currentShape.addPoint(screenModel.currentPosition)
                                    }
                                }

                                else -> Unit
                            }
                        }

                        PointerEvent.DragStart -> {
                            // Canvas does not redraw on DragStart for some reason here,
                            // so we moved all logic to drawModifier
                        }

                        PointerEvent.Drag -> {
                            if (screenModel.isMoveSelectionDrawMode) {
                                screenModel.previousPosition = screenModel.currentPosition
                            } else {
                                when (screenModel.currentMenuButtonAction) {
                                    MenuAction.DrawPolygon -> {
                                        screenModel.currentShape.setLastPoint(screenModel.currentPosition)
                                    }

                                    MenuAction.DrawRectangle,
                                    MenuAction.DoSelection -> {
                                        screenModel.setRectShapeAsCurrentShape()
                                    }

                                    else -> Unit
                                }
                            }
                        }

                        PointerEvent.DragEnd -> {
                            if (!screenModel.isMoveSelectionDrawMode && screenModel.currentMenuButtonAction != MenuAction.DrawPolygon) {
                                // Pointer is up save current path

                                if (screenModel.isSelectionAction.not() && screenModel.isPolygonAction.not()) {
                                    screenModel.shapes.add(screenModel.currentShape)
                                }

                                if (screenModel.isSelectionAction) {
                                    screenModel.showHandlesIfNeeded()
                                }

                                // Create new instance of path properties to have new path and properties
                                // only for the one currently being drawn
                                val properties = PathProperties(
                                    strokeWidth = screenModel.currentShape.properties.strokeWidth,
                                    strokeColor = screenModel.currentShape.properties.strokeColor,
                                    strokeCap = screenModel.currentShape.properties.strokeCap,
                                    strokeJoin = screenModel.currentShape.properties.strokeJoin,
                                )

                                // Since paths are keys for map, use new one for each key
                                // and have separate path for each down-move-up gesture cycle
                                screenModel.currentShape = Shape(properties)
                            }

                            // Since new path is drawn no need to store paths to undone
                            screenModel.shapesUndone.clear()

                            // If we leave this state at MotionEvent.Up it causes current path to draw
                            // line from (0,0) if this composable recomposes when draw mode is changed
                            screenModel.currentPosition = Offset.Unspecified
                            screenModel.previousPosition = screenModel.currentPosition

                            when (screenModel.currentMenuButtonAction) {
                                MenuAction.DrawRectangle, MenuAction.DoSelection -> {
                                    screenModel.pointerEvent = PointerEvent.Idle
                                }
                                else -> Unit
                            }
                        }

                        else -> Unit
                    }

                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)

                        // draw all paths

                        screenModel.shapes.forEach {
                            if (it.isSelected.not()) {
                                it.isSelected = screenModel.isSelectionAction && it.intersects(screenModel.currentShape)
                            }

                            it.draw(this@Canvas)
                        }

                        // draw current path

                        if (screenModel.pointerEvent != PointerEvent.Idle/* && pointerEvent != PointerEvent.Tap*/) {
                            val pathProperties =
                                if (screenModel.isSelectionAction) screenModel.currentShape.selectionPathProperties else screenModel.currentShape.properties

                            screenModel.currentShape.draw(this@Canvas, pathProperties)
                        }

                        restoreToCount(checkPoint)
                    }
                }

                ShapesMenu(
                    visible = screenModel.shapesMenuVisible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    selectedMenuAction = screenModel.currentMenuButtonAction,
                    onIconClick = {
                        screenModel.shapesMenuButton = it
                        screenModel.currentMenuButtonAction = it.menuAction
                        screenModel.shapesMenuVisible = false
                    }
                )

                PolygonActionsMenu(
                    visible = screenModel.polygonMenuVisible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    onButtonClick = {
                        screenModel.pointerEvent = PointerEvent.Idle
                        screenModel.polygonMenuVisible = false

                        if (it == MenuAction.PolygonApply) {
                            screenModel.shapes.add(screenModel.currentShape.copy())
                            screenModel.currentShape.reset()
                        } else {
                            screenModel.currentShape.reset()
                        }
                    }
                )

                DrawingPropertiesMenu(
                    modifier = Modifier
                        .shadow(1.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(4.dp),
                    pathProperties = screenModel.currentShape.properties,
                    shapesMenuButton = screenModel.shapesMenuButton,
                    shapesMenuButtonSelected = screenModel.shapesMenuButtonSelected,
                    onShapesMenuButtonClick = {
                        // show shapes menu on second button click only
                        screenModel.shapesMenuVisible = !screenModel.shapesMenuVisible && screenModel.shapesMenuButtonSelected
                        screenModel.shapesMenuButtonSelected = true
                        screenModel.currentMenuButtonAction = it
                        screenModel.selectionButtonSelected = false
                        screenModel.clearSelection()
                    } ,
                    selectionButtonSelected = screenModel.selectionButtonSelected,
                    onSelectionButtonClick = {
                        screenModel.currentMenuButtonAction = it
                        screenModel.shapesMenuButtonSelected = false
                        screenModel.selectionButtonSelected = true
                    }
                )

            }
        }

    }

}