package org.example.pdfConverter.controller

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.service.PdfEditor
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.render.PdfRenderManager

class PdfViewerController {

    private lateinit var viewModel: PdfUpdateViewModel
    private lateinit var view: PdfViewerView

    private var members: List<Member> = emptyList()
    private var common: CommonData? = null

    private val initializer = PdfViewerInitializer()

    fun createView(stage: Stage): BorderPane {

        // ▼ 初期データ読み込み
        val initialData = try {
            initializer.loadInitialData()
        } catch (e: Exception) {
            showError("初期データ読込エラー", e.message ?: "")
            return BorderPane()
        }

        members = initialData.members
        common = initialData.common

        // ▼ ViewModel
        viewModel = PdfUpdateViewModel(
            PdfEditor(),
            PdfRepository(),
            PdfRenderManager()
        )

        // ▼ View（UI構築はすべてこちら）
        view = PdfViewerView(members)

        // ▼ PDF 初期表示
        viewModel.loadPdf(initialData.templatePdf)
        view.imageView.imageProperty().bind(viewModel.currentImage)

        // ▼ イベント設定
        setupEvents(stage)

        return view.root
    }

    private fun setupEvents(stage: Stage) {

        // ▼ PDF 更新処理
        fun updatePdf() {
            val loadedCommon = common ?: return
            val selectedMember = members.firstOrNull { it.name == view.combo.value }

            viewModel.updatePdf(
                member = selectedMember,
                common = loadedCommon,
                reason = view.reasonArea.text,
                date = view.applyDateInput.getDate()
            )
        }

        // ▼ イベント
        view.combo.setOnAction { updatePdf() }
        view.applyDateInput.setOnChange { updatePdf() }
        view.reasonArea.textProperty().addListener { _, _, _ -> updatePdf() }

        // ▼ 保存ボタン
        view.exportButton.setOnAction {
            val chooser = FileChooser().apply {
                title = "保存先を選択"
                initialFileName = "output.pdf"
                extensionFilters.add(FileChooser.ExtensionFilter("PDF Files", "*.pdf"))
            }

            val saveFile = chooser.showSaveDialog(stage) ?: return@setOnAction

            try {
                viewModel.exportPdfTo(saveFile)
            } catch (e: Exception) {
                showError("PDF の保存に失敗しました", "原因: ${e.message}")
            }
        }
    }

    fun dispose() {
        viewModel.dispose()
    }

    private fun showError(title: String, message: String) {
        Platform.runLater {
            Alert(Alert.AlertType.ERROR).apply {
                this.title = title
                headerText = null
                contentText = message
            }.showAndWait()
        }
    }
}
