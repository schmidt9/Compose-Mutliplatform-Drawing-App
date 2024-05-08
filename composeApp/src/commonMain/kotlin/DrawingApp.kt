import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.smarttoolfactory.composedrawingapp.ui.theme.ComposeDrawingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun DrawingApp() {
    ComposeDrawingAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Navigator(screen = HomeScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}