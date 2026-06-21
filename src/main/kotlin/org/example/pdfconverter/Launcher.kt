package org.example.pdfconverter

import javafx.application.Application
import org.example.pdfconverter.view.PdfViewer

object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(PdfViewer::class.java, *args)
    }
}