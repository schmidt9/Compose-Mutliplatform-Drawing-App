package ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import util.PermissionCallback
import util.PermissionStatus
import util.PermissionType
import util.createPermissionsManager
import util.rememberCameraManager
import util.rememberGalleryManager

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AddImageDialog(
    onImageLoading: () -> Unit,
    onImageSelected: (ImageBitmap) -> Unit,
    onCancelled: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showImageSourceOptionDialog by remember { mutableStateOf(value = true) }
    var launchCamera by remember { mutableStateOf(value = false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }
    var permissionRationalDialog by remember { mutableStateOf(value = false) }
    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(
            permissionType: PermissionType,
            status: PermissionStatus
        ) {
            when (status) {
                PermissionStatus.GRANTED -> {
                    when (permissionType) {
                        PermissionType.CAMERA -> launchCamera = true
                        PermissionType.GALLERY -> launchGallery = true
                    }
                }

                else -> {
                    permissionRationalDialog = true
                }
            }
        }
    })

    val cameraManager = rememberCameraManager {
        coroutineScope.launch {
            onImageLoading()

            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }

            if (bitmap != null) {
                onImageSelected(bitmap)
            }

            onDismissRequest()
        }
    }

    val galleryManager = rememberGalleryManager {
        coroutineScope.launch {
            onImageLoading()

            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }

            if (bitmap != null) {
                onImageSelected(bitmap)
            }

            onDismissRequest()
        }
    }

    if (showImageSourceOptionDialog) {
        ImageSourceOptionDialog(onDismissRequest = {
            showImageSourceOptionDialog = false
            onCancelled()
            onDismissRequest()
        }, onGalleryRequest = {
            showImageSourceOptionDialog = false
            launchGallery = true
        }, onCameraRequest = {
            showImageSourceOptionDialog = false
            launchCamera = true
        })
    }

    if (launchGallery) {
        if (permissionsManager.isPermissionGranted(PermissionType.GALLERY)) {
            galleryManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.GALLERY)
        }
        launchGallery = false
    }

    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }

    if (launchSetting) {
        permissionsManager.launchSettings()
        launchSetting = false
    }

    if (permissionRationalDialog) {
        AlertMessageDialog(
            title = "Permission Required",
            message = "To add an image to the drawing please grant this permission. You can manage permissions in your device settings.",
            positiveButtonText = "Settings",
            negativeButtonText = "Cancel",
            onPositiveClick = {
                permissionRationalDialog = false
                launchSetting = true

            },
            onNegativeClick = {
                permissionRationalDialog = false
            })
    }

}