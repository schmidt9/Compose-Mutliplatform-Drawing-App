package ui.menu.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import composemutliplatformdrawingapp.composeapp.generated.resources.Res
import composemutliplatformdrawingapp.composeapp.generated.resources.ic_camera
import composemutliplatformdrawingapp.composeapp.generated.resources.ic_images
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ImageSourceOptionDialog(
    onDismissRequest: () -> Unit,
    onGalleryRequest: () -> Unit = {},
    onCameraRequest: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)
                .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select an Image Source",
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp).clickable {
                    onCameraRequest.invoke()
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(Res.drawable.ic_camera),
                    contentDescription = null
                )
                Text(text = "Camera", color = MaterialTheme.colors.onSurface)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp).clickable {
                    onGalleryRequest.invoke()
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(Res.drawable.ic_images),
                    contentDescription = null
                )
                Text(text = "Gallery", color = MaterialTheme.colors.onSurface)
            }
        }
    }
}