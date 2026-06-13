package org.example.pdfconverter

import javafx.application.Application

object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(PdfViewer::class.java, *args)
    }
}