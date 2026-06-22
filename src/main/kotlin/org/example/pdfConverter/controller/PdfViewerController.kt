package org.example.pdfConverter.controller

import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.service.PdfEditor
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.render.PdfRenderManager

class PdfViewerController(
    private val errorHandler: ErrorHandler
) {

    private var viewModel: PdfUpdateViewModel? = null
    private lateinit var view: PdfViewerView

    private var members: List<Member> = emptyList()
    private var common: CommonData? = null

    private val initializer = PdfViewerInitializer()

    fun createView(stage: Stage): BorderPane {

        // ▼ 初期データ読み込み
        val initialData = try {
            initializer.loadInitialData()
        } catch (e: Exception) {
            errorHandler.showError(e.message)
            return BorderPane()
        }

        members = initialData.members
        common = initialData.common

        // ▼ ViewModel
        val vm = PdfUpdateViewModel(
            PdfEditor(),
            PdfRepository(),
            PdfRenderManager()
        )
        viewModel = vm

        // ▼ View（UI構築はすべてこちら）
        view = PdfViewerView(members)

        // ▼ PDF 初期表示
        vm.loadPdf(initialData.templatePdf)
        view.imageView.imageProperty().bind(vm.currentImage)

        // ▼ イベント設定（EventBinder に委譲）
        PdfViewerEventBinder(
            view = view,
            viewModel = vm,
            members = members,
            common = common,
            stage = stage,
            errorHandler = errorHandler
        ).bind()

        return view.root
    }

    fun dispose() {
        viewModel?.dispose()
    }
}
