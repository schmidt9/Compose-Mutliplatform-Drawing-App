package util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraManager(onResult: (SharedImage?) -> Unit): CameraManager {
    TODO("Not yet implemented")
}

actual class CameraManager actual constructor(onLaunch: () -> Unit) {
    actual fun launch() {
    }
}