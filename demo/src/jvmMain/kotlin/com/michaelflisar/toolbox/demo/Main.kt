package com.michaelflisar.toolbox.demo

import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            Text("App is running...")
        }
    }
}
