package org.example.pdfConverter.view

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.example.pdfConverter.controller.PdfViewerController
import org.example.pdfConverter.service.ErrorHandlerImpl

class PdfViewer : Application() {

    override fun start(stage: Stage) {

        val controller = PdfViewerController(
            errorHandler = ErrorHandlerImpl()
        )
        val root = controller.createView(stage)

        stage.scene = Scene(root, 900.0, 600.0)
        stage.title = "CareDoc"
        stage.show()

        stage.setOnCloseRequest {
            controller.dispose()
        }
    }
}

fun main() {
    Application.launch(PdfViewer::class.java)
}
