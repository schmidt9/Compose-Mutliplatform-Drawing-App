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
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.smarttoolfactory.composedrawingapp.ui.theme.backgroundColor
import gesture.PointerEvent
import gesture.pointerEvents
import model.PathProperties
import ui.dialogs.AddImageDialog
import ui.dialogs.LoadingIndicator
import ui.graphics.ImageShape
import ui.graphics.Point
import ui.graphics.PolygonShape
import ui.graphics.Shape
import ui.menu.DrawingPropertiesMenu
import ui.menu.HomeScreenTopMenu
import ui.menu.MenuAction
import ui.menu.PolygonActionsMenu
import ui.menu.SelectionActionsMenu
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
                            handleModifierTap(screenModel, it)
                        },
                        onDoubleTap = {
                            handleModifierDoubleTap(screenModel, it)
                        },
                        onLongPress = {
                            handleModifierLongPress(screenModel, it)
                        },
                        onDragStart = {
                            handleModifierDragStart(screenModel, it)
                        },
                        onDrag = { position, dragAmount ->
                            handleModifierDrag(screenModel, position, dragAmount)
                        },
                        onDragEnd = {
                            handleModifierDragEnd(screenModel)
                        },
                        onPressReleased = {
                            handleModifierPressReleased(screenModel, it)
                        },
                        onTransform = { centroid, pan, zoom ->
                            handleModifierTransform(screenModel, centroid, pan, zoom)
                        },
                        onPointerUp = {
                            handleModifierPointerUp(screenModel)
                        }
                    )

                Canvas(modifier = drawModifier) {
                    when (screenModel.pointerEvent) {
                        PointerEvent.Drag -> {
                            handleCanvasDrag(screenModel)
                        }

                        PointerEvent.DragEnd -> {
                            handleCanvasDragEnd(screenModel)
                        }

                        else -> Unit
                    }

                    handleCanvasDrawing(screenModel, drawContext, this)
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
                            screenModel.addCurrentShape()
                            screenModel.currentShape.reset()
                        } else {
                            screenModel.currentShape.reset()
                        }
                    }
                )

                SelectionActionsMenu(
                    visible = screenModel.selectionActionsMenuVisible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    onButtonClick = {
                        screenModel.pointerEvent = PointerEvent.Idle
                        screenModel.selectionActionsMenuVisible = false

                        screenModel.removeSelection()
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
                        screenModel.shapesMenuVisible =
                            !screenModel.shapesMenuVisible && screenModel.shapesMenuButtonSelected
                        screenModel.shapesMenuButtonSelected = true
                        screenModel.currentMenuButtonAction = it
                        screenModel.selectionButtonSelected = false
                        screenModel.addImageButtonSelected = false
                        screenModel.clearSelection()
                    },
                    selectionButtonSelected = screenModel.selectionButtonSelected,
                    onSelectionButtonClick = {
                        screenModel.currentMenuButtonAction = it
                        screenModel.shapesMenuButtonSelected = false
                        screenModel.selectionButtonSelected = true
                        screenModel.addImageButtonSelected = false
                    },
                    addImageButtonSelected = screenModel.addImageButtonSelected,
                    onAddImageButtonClick = {
                        screenModel.currentMenuButtonAction = it
                        screenModel.shapesMenuButtonSelected = false
                        screenModel.selectionButtonSelected = false
                        screenModel.addImageButtonSelected = true
                    }
                )

                if (screenModel.addImageDialogVisible) {
                    AddImageDialog(
                        onImageLoading = {
                            screenModel.imageIsLoading = true
                        },
                        onImageSelected = {
                            screenModel.addImageDialogVisible = false
                            screenModel.imageIsLoading = false
                            screenModel.addCurrentImageShape(it)
                        },
                        onCancelled = {
                            screenModel.currentShape = Shape()
                            screenModel.addImageDialogVisible = false
                        },
                        onDismissRequest = {
                            screenModel.addImageDialogVisible = false
                        }
                    )
                }

            }

            if (screenModel.imageIsLoading) {
                LoadingIndicator(text = "Loading image...")
            }
        }

    }

    private fun handleModifierTap(screenModel: HomeScreenModel, point: Point) {
        screenModel.pointerEvent = PointerEvent.Tap

        screenModel.currentPosition = point
        screenModel.previousPosition = point

        screenModel.clearSelection()

        when (screenModel.currentMenuButtonAction) {
            MenuAction.DoSelection,
            MenuAction.AddImage -> {
                screenModel.updateSelectionAtPoint(point)
            }

            else -> Unit
        }
    }

    private fun handleModifierDoubleTap(screenModel: HomeScreenModel, point: Point) {
        screenModel.pointerEvent = PointerEvent.DoubleTap

        screenModel.currentPosition = point
        screenModel.previousPosition = point

        when (screenModel.currentMenuButtonAction) {
            MenuAction.DoSelection -> {
                screenModel.handleDoubleTap(point)
            }

            else -> Unit
        }
    }

    private fun handleModifierLongPress(screenModel: HomeScreenModel, point: Point) {
        screenModel.pointerEvent = PointerEvent.LongPress

        screenModel.currentPosition = point
        screenModel.previousPosition = point

        when (screenModel.currentMenuButtonAction) {
            MenuAction.DoSelection -> {
                screenModel.handleLongPress(point)
            }

            else -> Unit
        }
    }

    private fun handleModifierDragStart(screenModel: HomeScreenModel, point: Point) {
        screenModel.pointerEvent = PointerEvent.DragStart

        screenModel.currentPosition = point
        screenModel.previousPosition = point

        screenModel.updateDrawMode(point)

        when (screenModel.currentMenuButtonAction) {
            MenuAction.DrawPolygon -> {
                if (screenModel.currentShape.isEmpty) {
                    screenModel.currentShape = PolygonShape(screenModel.currentPosition)
                } else {
                    screenModel.currentShape.addPoint(screenModel.currentPosition)
                }

                screenModel.polygonMenuVisible = true
            }

            MenuAction.DrawRectangle -> {
                screenModel.setRectShapeAsCurrentShape()
            }

            MenuAction.DoSelection -> {
                if (screenModel.isResizeSelectionDrawMode) {
                    screenModel.startCurrentShapeResizing(point)
                }
            }

            MenuAction.AddImage -> {
                if (screenModel.hasSelection) {
                    screenModel.startCurrentShapeResizing(point)
                } else {
                    screenModel.setAddImageShapeAsCurrentShape()
                }
            }

            else -> Unit
        }

        screenModel.shapesMenuVisible = false
    }

    private fun handleModifierDrag(
        screenModel: HomeScreenModel,
        position: Point,
        dragAmount: Offset
    ) {
        screenModel.pointerEvent = PointerEvent.Drag
        screenModel.currentPosition = position

        if (screenModel.isMoveSelectionDrawMode) {
            screenModel.translateSelectedShapes(dragAmount)
        } else if (screenModel.isResizeSelectionDrawMode) {
            screenModel.resizeCurrentShape(position)
        }
    }

    private fun handleCanvasDrag(screenModel: HomeScreenModel) {
        if (screenModel.isMoveSelectionDrawMode) {
            screenModel.previousPosition = screenModel.currentPosition
        } else {
            when (screenModel.currentMenuButtonAction) {
                MenuAction.DrawPolygon -> {
                    screenModel.currentShape.setLastPoint(screenModel.currentPosition)
                }

                MenuAction.DrawRectangle -> {
                    screenModel.setRectShapeAsCurrentShape()
                }

                MenuAction.DoSelection -> {
                    when (screenModel.drawMode) {
                        DrawMode.Draw -> {
                            screenModel.setRectShapeAsCurrentShape()
                        }

                        DrawMode.ResizeSelection -> {
                            screenModel.previousPosition = screenModel.currentPosition
                        }

                        else -> Unit
                    }
                }

                MenuAction.AddImage -> {
                    if (screenModel.hasSelection) {
                        screenModel.previousPosition = screenModel.currentPosition
                    } else {
                        screenModel.setAddImageShapeAsCurrentShape()
                    }
                }

                else -> Unit
            }
        }
    }

    private fun handleModifierDragEnd(screenModel: HomeScreenModel) {
        screenModel.pointerEvent = PointerEvent.DragEnd
        screenModel.endCurrentShapeResizing()

        if (screenModel.isAddImageAction && screenModel.hasSelection.not()) {
            screenModel.addImageDialogVisible = true
        }
    }

    private fun handleCanvasDragEnd(screenModel: HomeScreenModel) {
        if (!screenModel.isMoveSelectionDrawMode && screenModel.currentMenuButtonAction != MenuAction.DrawPolygon) {
            // Pointer is up save current path

            if (screenModel.isSelectionAction.not() &&
                screenModel.isPolygonAction.not() &&
                screenModel.isAddImageAction.not()) {
                screenModel.addCurrentShape()
            }

            // Create new instance of path properties to have new path and properties
            // only for the one currently being drawn
            val properties = PathProperties(
                strokeWidth = screenModel.currentShape.properties.strokeWidth,
                strokeColor = screenModel.currentShape.properties.strokeColor,
                strokeCap = screenModel.currentShape.properties.strokeCap,
                strokeJoin = screenModel.currentShape.properties.strokeJoin,
            )

            if (screenModel.isPolygonAction.not() &&
                screenModel.isAddImageAction.not()) {
                // Since paths are keys for map, use new one for each key
                // and have separate path for each down-move-up gesture cycle
                screenModel.currentShape = Shape(properties)
            }
        }

        // Since new path is drawn no need to store paths to undone
        screenModel.shapesUndone.clear()

        // If we leave this state at MotionEvent.Up it causes current path to draw
        // line from (0,0) if this composable recomposes when draw mode is changed
        screenModel.currentPosition = Offset.Unspecified
        screenModel.previousPosition = screenModel.currentPosition

        when (screenModel.currentMenuButtonAction) {
            MenuAction.DrawRectangle,
            MenuAction.DoSelection,
            MenuAction.AddImage -> {
                screenModel.pointerEvent = PointerEvent.Idle
            }

            else -> Unit
        }
    }

    private fun handleModifierPressReleased(screenModel: HomeScreenModel, point: Point) {
        screenModel.pointerEvent = PointerEvent.Idle

        screenModel.currentPosition = point
        screenModel.previousPosition = point
    }

    private fun handleModifierTransform(screenModel: HomeScreenModel,
                                        centroid: Point,
                                        pan: Offset,
                                        zoom: Float) {
        screenModel.pointerEvent = PointerEvent.Zoom

        screenModel.handlePan(pan)
        screenModel.handleZoom(centroid, zoom)

        screenModel.pointerEvent = PointerEvent.Idle // trigger canvas redrawing
    }

    private fun handleModifierPointerUp(screenModel: HomeScreenModel) {
        screenModel.pointerEvent = PointerEvent.Idle
    }

    private fun handleCanvasDrawing(
        screenModel: HomeScreenModel,
        drawContext: DrawContext,
        drawScope: DrawScope
    ) {
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // draw all shapes

            screenModel.updateSelection()

            screenModel.drawShapes(drawScope)

            // draw current shape

            if (screenModel.pointerEvent != PointerEvent.Idle/* && pointerEvent != PointerEvent.Tap*/) {
                screenModel.drawCurrentShape(drawScope)
            }

            restoreToCount(checkPoint)
        }
    }

}