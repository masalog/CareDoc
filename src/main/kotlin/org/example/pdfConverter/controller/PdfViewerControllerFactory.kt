package org.example.pdfConverter.controller

import javafx.stage.Stage
import org.example.pdfConverter.factory.PdfViewerFactory
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.service.PdfViewerInitializer

object PdfViewerControllerFactory {

    fun create(errorHandler: ErrorHandler): PdfViewerController {
        return PdfViewerController(
            errorHandler = errorHandler,
            initializer = PdfViewerInitializer(),
            factory = PdfViewerFactory(),
            binder = { view, vm, members, common, stage, err ->
                PdfViewerEventBinder(
                    view = view,
                    viewModel = vm,
                    members = members,
                    common = common,
                    stage = stage,
                    errorHandler = err
                ).bind()
            }
        )
    }
}