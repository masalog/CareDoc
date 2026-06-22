package org.example.pdfConverter.controller

import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.factory.PdfViewerFactory


class PdfViewerController(
    private val errorHandler: ErrorHandler,
    private val initializer: PdfViewerInitializer = PdfViewerInitializer(),
    private val factory: PdfViewerFactory = PdfViewerFactory()
) {

    private var viewModel: PdfUpdateViewModel? = null
    private lateinit var view: PdfViewerView

    fun createView(stage: Stage): BorderPane {

        val initialData = runCatching { initializer.loadInitialData() }
            .getOrElse {
                errorHandler.showError("初期データ読込エラー", it.message)
                return BorderPane()
            }

        val (vm, v) = factory.create(initialData)
        viewModel = vm
        view = v

        PdfViewerEventBinder(
            view = view,
            viewModel = vm,
            members = initialData.members,
            common = initialData.common,
            stage = stage,
            errorHandler = errorHandler
        ).bind()

        return view.root
    }

    fun dispose() {
        viewModel?.dispose()
    }
}
