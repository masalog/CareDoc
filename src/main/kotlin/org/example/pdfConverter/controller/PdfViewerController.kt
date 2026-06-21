package org.example.pdfConverter.controller

import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.repository.ExcelLoader
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.render.PdfRenderManager
import org.example.pdfConverter.service.PdfEditor
import org.example.pdfConverter.service.PdfLoader
import org.example.pdfConverter.view.PdfViewerViewFactory
import org.example.pdfConverter.viewModel.DateInputViewModel
import org.example.pdfConverter.viewModel.PdfUpdateViewModel

class PdfViewerController {

    private lateinit var imageView: ImageView
    private lateinit var viewModel: PdfUpdateViewModel

    private var members: List<Member> = emptyList()
    private var common: CommonData? = null

    private val pdfLoader = PdfLoader()
    private val uiFactory = PdfViewerViewFactory()

    fun createView(stage: Stage): BorderPane {

        val root = BorderPane()

        // ▼ タイトル（Factory）
        val titleLabel = uiFactory.createTitle()
        root.top = titleLabel

        // ▼ PDF 表示エリア（Factory）
        imageView = ImageView()
        val scrollPane = uiFactory.createPdfScrollPane(imageView)
        root.center = scrollPane

        // ▼ ViewModel
        viewModel = PdfUpdateViewModel(
            PdfEditor(),
            PdfRepository(),
            PdfRenderManager()
        )
        imageView.imageProperty().bind(viewModel.currentImage)

        // ▼ UI コンポーネント
        val combo = ComboBox<String>()
        val header = "名前を選択してください"
        combo.items.add(header)
        combo.value = header
        combo.prefWidth = 250.0

        val reasonArea = TextArea().apply {
            promptText = "変更申請の理由を入力してください"
            prefRowCount = 2
            isWrapText = true
        }

        val applyDateInput = DateInputViewModel()
        val applyDateBox = applyDateInput.toHBox()

        // ▼ Excel 読み込み
        try {
            val result = ExcelLoader.loadAll()
            members = result.first
            common = result.second
            combo.items.addAll(members.map { it.name })
        } catch (e: Exception) {
            showError("Excel 読み込みエラー", "members.xlsx を読み込めませんでした。\n${e.message}")
        }

        // ▼ PDF 更新処理
        fun updatePdf() {
            val loadedCommon = common ?: return
            val member =
                if (combo.value == header) null
                else members.firstOrNull { it.name == combo.value }

            viewModel.updatePdf(
                member = member,
                common = loadedCommon,
                reason = reasonArea.text,
                date = applyDateInput.getDate()
            )
        }

        // ▼ イベント
        combo.setOnAction { updatePdf() }
        applyDateInput.setOnChange { updatePdf() }
        reasonArea.textProperty().addListener { _, _, _ ->
            if (common != null) updatePdf()
        }

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

        // ▼ 下部 UI（Factory）
        val bottom = uiFactory.createBottomPanel(
            combo = combo,
            applyDateBox = applyDateBox,
            reasonArea = reasonArea,
            exportButton = exportButton
        )
        root.bottom = bottom

        // ▼ 起動時テンプレート読み込み
        loadTemplatePdf()

        return root
    }

    private fun loadTemplatePdf() {
        try {
            val file = pdfLoader.loadTemplatePdf()
            viewModel.loadPdf(file)
        } catch (e: Exception) {
            showError("テンプレート読込エラー", "template.pdf を読み込めませんでした。\n${e.message}")
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
