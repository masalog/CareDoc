package org.example.pdfConverter.view

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.example.pdfConverter.controller.PdfViewerControllerFactory
import org.example.pdfConverter.service.ErrorHandlerImpl

class PdfViewer : Application() {

    override fun start(stage: Stage) {

        val errorHandler = ErrorHandlerImpl()

        // ✅ FactoryでController生成
        val controller = PdfViewerControllerFactory.create(errorHandler)

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