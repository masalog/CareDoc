package org.example.pdfConverter.factory

import org.example.pdfConverter.model.InitialData
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.service.PdfEditor
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.render.PdfRenderManager

/**
 * PdfViewer の View と ViewModel をまとめて生成する Factory クラス。
 * Controller の肥大化を防ぐために導入。
 */
class PdfViewerFactory(
    private val pdfEditor: PdfEditor = PdfEditor(),
    private val pdfRepository: PdfRepository = PdfRepository(),
    private val pdfRenderManager: PdfRenderManager = PdfRenderManager()
) {

    fun create(initialData: InitialData): Pair<PdfUpdateViewModel, PdfViewerView> {

        val viewModel = PdfUpdateViewModel(
            pdfEditor,
            pdfRepository,
            pdfRenderManager
        )

        val view = PdfViewerView(initialData.members)

        viewModel.loadPdf(initialData.templatePdf)

        view.imageView.imageProperty().bind(viewModel.currentImage)

        return viewModel to view
    }
}
