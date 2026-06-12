package org.example.caredoc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.caredoc.ui.MainScreen

fun main() = application {

    // PDFBox のフォントスキャンを無効化
    System.setProperty("pdfbox.fontcache", "false")
    System.setProperty("pdfbox.skipFontLoading", "true")

    Window(
        onCloseRequest = ::exitApplication,
        title = "CareDoc"
    ) {
        MainScreen()
    }
}
