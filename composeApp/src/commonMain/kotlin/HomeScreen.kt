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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import gesture.PointerEvent
import com.smarttoolfactory.composedrawingapp.ui.theme.backgroundColor
import gesture.pointerEvents
import model.PathProperties
import ui.graphics.RectShape
import ui.graphics.ShapePath
import ui.menu.DrawingPropertiesMenu
import ui.menu.HomeScreenTopMenu
import ui.menu.MenuAction
import ui.menu.MenuButton
import ui.menu.ShapesMenu
import kotlin.math.abs
import kotlin.math.min

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        /**
         * Paths that are added, this is required to have paths with different options and paths
         *  ith erase to keep over each other
         */
        val paths = remember { mutableStateListOf<ShapePath>() }

        /**
         * Paths that are undone via button. These paths are restored if user pushes
         * redo button if there is no new path drawn.
         *
         * If new path is drawn after this list is cleared to not break paths after undoing previous
         * ones.
         */
        val pathsUndone = remember { mutableStateListOf<ShapePath>() }

        /**
         * Canvas touch state. [PointerEvent.Idle] by default, [PointerEvent.DragStart] at first contact,
         * [PointerEvent.Drag] while dragging and [PointerEvent.DragEnd] when first pointer is up
         */
        var pointerEvent by remember { mutableStateOf(PointerEvent.Idle) }

        /**
         * Current position of the pointer that is pressed or being moved
         */
        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

        /**
         * Previous motion event before next touch is saved into this current position.
         */
        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

        /**
         * Draw mode, erase mode or touch mode to
         */
        var drawMode by remember { mutableStateOf(DrawMode.Draw) }

        /**
         * Path that is being drawn between [PointerEvent.DragStart] and [PointerEvent.DragEnd]. When
         * pointer is up this path is saved to **paths** and new instance is created
         */
        var currentPath by remember { mutableStateOf(ShapePath()) }

        var shapesMenuButtonSelected by remember { mutableStateOf(true) }

        var selectionButtonSelected by remember { mutableStateOf(false) }

        var shapesMenuVisible by remember { mutableStateOf(false) }

        var currentMenuButtonAction by remember { mutableStateOf(MenuButton.DrawRectangleMenuButton.menuAction) }

        fun selectedPaths() = paths.filter { it.isSelected }

        val isSelectionAction by remember { derivedStateOf { currentMenuButtonAction == MenuAction.DoSelection } }

        val isMoveSelectionDrawMode by remember { derivedStateOf { drawMode == DrawMode.MoveSelection } }

        Scaffold(topBar = {
            TopAppBar(
                elevation = 2.dp,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                title = {},
                actions = {
                    HomeScreenTopMenu(
                        onUndo = {
                            if (paths.isNotEmpty()) {
                                val lastItem = paths.last()
                                paths.remove(lastItem)
                                pathsUndone.add(lastItem)
                            }
                        },
                        onRedo = {
                            if (pathsUndone.isNotEmpty()) {
                                val lastPath = pathsUndone.last()
                                pathsUndone.removeLast()
                                paths.add(lastPath)
                            }
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
                            pointerEvent = PointerEvent.Tap

                            paths.forEach {
                                it.isSelected = false
                            }
                        },
                        onDoubleTap = {
                            // TODO: impl
                        },
                        onDragStart = {
                            pointerEvent = PointerEvent.DragStart

                            drawMode = if (isSelectionAction) {
                                if (selectedPaths().isEmpty()) DrawMode.Draw else DrawMode.MoveSelection
                            } else {
                                DrawMode.Draw
                            }

                            currentPosition = it
                            previousPosition = it
                        },
                        onDrag = { position, dragAmount ->
                            pointerEvent = PointerEvent.Drag
                            currentPosition = position

                            if (isMoveSelectionDrawMode) {
                                selectedPaths().forEach { path ->
                                    path.translate(dragAmount)
                                }
                            }
                        },
                        onDragEnd = {
                            pointerEvent = PointerEvent.DragEnd
                        }
                    )

                Canvas(modifier = drawModifier) {

                    when (pointerEvent) {
                        PointerEvent.DragStart -> {
                            previousPosition = currentPosition

                            shapesMenuVisible = false
                        }

                        PointerEvent.Drag -> {
                            if (isMoveSelectionDrawMode) {
                                previousPosition = currentPosition
                            } else {
                                when (currentMenuButtonAction) {
                                    MenuAction.DrawPolygon -> {
                                        TODO("impl")
                                    }

                                    MenuAction.DrawRectangle,
                                    MenuAction.DoSelection -> {
                                        val left = min(previousPosition.x, currentPosition.x)
                                        val top = min(previousPosition.y, currentPosition.y)
                                        val right =
                                            left + abs(previousPosition.x - currentPosition.x)
                                        val bottom =
                                            top + abs(previousPosition.y - currentPosition.y)

                                        val rect = Rect(
                                            left = left,
                                            top = top,
                                            right = right,
                                            bottom = bottom
                                        )

                                        currentPath = RectShape(rect)
                                    }

                                    MenuAction.None -> {}
                                }
                            }
                        }

                        PointerEvent.DragEnd -> {
                            if (drawMode != DrawMode.MoveSelection) {
                                // Pointer is up save current path

                                if (isSelectionAction.not()) {
                                    paths.add(currentPath)
                                }

                                // Create new instance of path properties to have new path and properties
                                // only for the one currently being drawn
                                val properties = PathProperties(
                                    strokeWidth = currentPath.properties.strokeWidth,
                                    color = currentPath.properties.color,
                                    strokeCap = currentPath.properties.strokeCap,
                                    strokeJoin = currentPath.properties.strokeJoin,
                                )

                                // Since paths are keys for map, use new one for each key
                                // and have separate path for each down-move-up gesture cycle
                                currentPath = ShapePath(properties)
                            }

                            // Since new path is drawn no need to store paths to undone
                            pathsUndone.clear()

                            // If we leave this state at MotionEvent.Up it causes current path to draw
                            // line from (0,0) if this composable recomposes when draw mode is changed
                            currentPosition = Offset.Unspecified
                            previousPosition = currentPosition
                            pointerEvent = PointerEvent.Idle
                        }

                        else -> Unit
                    }

                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)

                        // draw all paths

                        paths.forEach {
                            if (it.isSelected.not()) {
                                it.isSelected = isSelectionAction && it.intersects(currentPath)
                            }

                            it.draw(this@Canvas)
                        }

                        // draw current path

                        if (pointerEvent != PointerEvent.Idle && pointerEvent != PointerEvent.Tap) {
                            val pathProperties =
                                if (isSelectionAction) currentPath.selectionPathProperties else currentPath.properties

                            currentPath.draw(this@Canvas, pathProperties)
                        }

                        restoreToCount(checkPoint)
                    }
                }

                ShapesMenu(
                    visible = shapesMenuVisible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    selectedMenuAction = currentMenuButtonAction,
                    onIconClick = {
                        currentMenuButtonAction = it.menuAction
                        shapesMenuVisible = false
                    }
                )

                DrawingPropertiesMenu(
                    modifier = Modifier
                        .shadow(1.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(4.dp),
                    pathProperties = currentPath.properties,
                    shapesMenuButtonAction = currentMenuButtonAction,
                    shapesMenuButtonSelected = shapesMenuButtonSelected,
                    onShapesMenuButtonClick = {
                        shapesMenuVisible = !shapesMenuVisible
                        currentMenuButtonAction = it
                        shapesMenuButtonSelected = true
                        selectionButtonSelected = false
                    },
                    selectionButtonSelected = selectionButtonSelected,
                    onSelectionButtonClick = {
                        currentMenuButtonAction = it
                        shapesMenuButtonSelected = false
                        selectionButtonSelected = true
                    }
                )

            }
        }

    }

}