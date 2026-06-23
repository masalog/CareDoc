package org.example.pdfConverter.controller

import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.example.pdfConverter.factory.PdfViewerFactory
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel


class PdfViewerController(
    private val errorHandler: ErrorHandler,
    private val initializer: PdfViewerInitializer,
    private val factory: PdfViewerFactory,
    private val binder: (
        view: PdfViewerView,
        viewModel: PdfUpdateViewModel,
        members: List<Member>,
        common: CommonData?,
        stage: Stage,
        errorHandler: ErrorHandler
    ) -> Unit
) {

    private var viewModel: PdfUpdateViewModel? = null
    private lateinit var view: PdfViewerView

    fun createView(stage: Stage): BorderPane {

        // ✅ 例外はここで完全に吸収
        val initialData = try {
            initializer.loadInitialData()
        } catch (e: Exception) {
            errorHandler.showError("初期データ読込エラー", e.message)
            return BorderPane()
        }

        // ✅ View / ViewModel 生成
        val (vm, v) = factory.create(initialData)
        viewModel = vm
        view = v

        // ✅ イベントバインド
        binder(
            view,
            vm,
            initialData.members,
            initialData.common,
            stage,
            errorHandler
        )

        return view.root
    }

    fun dispose() {
        viewModel?.dispose()
    }
}