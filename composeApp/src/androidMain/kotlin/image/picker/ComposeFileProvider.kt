package image.picker

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import ru.ankorm.cmda.R
import java.io.File
import java.util.Objects

/**
 * https://github.com/QasimNawaz/KMPImagePicker/blob/main/composeApp/src/androidMain/kotlin/kmp/image/picker/ComposeFileProvider.kt
 */
class ComposeFileProvider : FileProvider(
    R.xml.path_provider
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            // 1
            val tempFile = File.createTempFile(
                "picture_${System.currentTimeMillis()}", ".png", context.cacheDir
            ).apply {
                createNewFile()
            }
            // 2
            val authority = context.applicationContext.packageName + ".provider"
            // 3
            println("getImageUri: ${tempFile.absolutePath}")
            return getUriForFile(
                Objects.requireNonNull(context),
                authority,
                tempFile,
            )
        }
    }
}