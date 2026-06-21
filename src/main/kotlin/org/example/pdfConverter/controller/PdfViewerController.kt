package org.example.pdfConverter.controller

import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.view.PdfViewerViewFactory
import org.example.pdfConverter.viewModel.DateInputViewModel
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.service.PdfEditor
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.render.PdfRenderManager

class PdfViewerController {

    private lateinit var imageView: ImageView
    private lateinit var viewModel: PdfUpdateViewModel

    private var members: List<Member> = emptyList()
    private var common: CommonData? = null

    private val initializer = PdfViewerInitializer()
    private val uiFactory = PdfViewerViewFactory()

    fun createView(stage: Stage): BorderPane {

        val root = BorderPane()

        // ▼ タイトル
        root.top = uiFactory.createTitle()

        // ▼ PDF 表示エリア
        imageView = ImageView()
        root.center = uiFactory.createPdfScrollPane(imageView)

        // ▼ ViewModel
        viewModel = PdfUpdateViewModel(
            PdfEditor(),
            PdfRepository(),
            PdfRenderManager()
        )
        imageView.imageProperty().bind(viewModel.currentImage)

        // ▼ 初期データ読み込み（Excel + template.pdf）
        val initialData = try {
            initializer.loadInitialData()
        } catch (e: Exception) {
            showError("初期データ読込エラー", e.message ?: "")
            return root
        }

        members = initialData.members
        common = initialData.common
        viewModel.loadPdf(initialData.templatePdf)

        // ▼ UI コンポーネント
        val combo = ComboBox<String>().apply {
            val header = "名前を選択してください"
            items.add(header)
            items.addAll(members.map { it.name })
            value = header
            prefWidth = 250.0
        }

        val reasonArea = TextArea().apply {
            promptText = "変更申請の理由を入力してください"
            prefRowCount = 2
            isWrapText = true
        }

        val applyDateInput = DateInputViewModel()
        val applyDateBox = applyDateInput.toHBox()

        // ▼ PDF 更新処理
        fun updatePdf() {
            val loadedCommon = common ?: return
            val selectedMember = members.firstOrNull { it.name == combo.value }

            viewModel.updatePdf(
                member = selectedMember,
                common = loadedCommon,
                reason = reasonArea.text,
                date = applyDateInput.getDate()
            )
        }

        // ▼ イベント
        combo.setOnAction { updatePdf() }
        applyDateInput.setOnChange { updatePdf() }
        reasonArea.textProperty().addListener { _, _, _ -> updatePdf() }

        // ▼ 保存ボタン
        val exportButton = Button("保存").apply {
            prefWidth = 120.0
            setOnAction {
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

        // ▼ 下部 UI
        root.bottom = uiFactory.createBottomPanel(
            combo = combo,
            applyDateBox = applyDateBox,
            reasonArea = reasonArea,
            exportButton = exportButton
        )

        return root
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
