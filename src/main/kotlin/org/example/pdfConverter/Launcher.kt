package org.example.pdfConverter

import javafx.application.Application
import org.example.pdfConverter.view.PdfViewer

object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(PdfViewer::class.java, *args)
    }
}