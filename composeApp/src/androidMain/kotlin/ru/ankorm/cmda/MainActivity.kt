package ru.ankorm.cmda

import DrawingApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrawingApp()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    DrawingApp()
}