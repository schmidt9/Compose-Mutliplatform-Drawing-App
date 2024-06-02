package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.model.ScreenModel
import ui.graphics.Shape

class HomeScreenModel : ScreenModel {

    /**
     * Paths that are added, this is required to have paths with different options and paths
     *  ith erase to keep over each other
     */
    val paths = mutableStateListOf<Shape>()

    /**
     * Paths that are undone via button. These paths are restored if user pushes
     * redo button if there is no new path drawn.
     *
     * If new path is drawn after this list is cleared to not break paths after undoing previous
     * ones.
     */
    val pathsUndone = mutableStateListOf<Shape>()

    val selectedPaths get() = paths.filter { it.isSelected }

}