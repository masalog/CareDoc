package org.example.pdfConverter.view

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.viewModel.DateInputViewModel
import org.example.pdfConverter.repository.ExcelLoader
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.PdfEditor
import java.io.File
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.render.PdfRenderManager

class PdfViewer : Application() {

    private lateinit var imageView: ImageView

    override fun start(stage: Stage) {

        val root = BorderPane()

        // タイトルラベル（title と衝突しないように変更）
        val titleLabel = Label("介護認定申請書 作成アプリ CareDoc")
        titleLabel.style = "-fx-font-size: 18px; -fx-padding: 10px;"
        root.top = titleLabel

        // PDF 表示エリア
        imageView = ImageView()
        val stack = StackPane(imageView)
        val scrollPane = ScrollPane(stack).apply { isPannable = true }
        root.center = scrollPane

        // 利用者選択
        val combo = ComboBox<String>()
        val header = "名前を選択してください"
        combo.items.add(header)
        combo.prefWidth = 250.0
        combo.value = header

        // 理由入力
        val reasonArea = TextArea().apply {
            promptText = "変更申請の理由を入力してください"
            prefRowCount = 2
            isWrapText = true
        }

        // 日付入力
        val applyDateInput = DateInputViewModel()

        // Excel 読み込み
        var members: List<Member> = emptyList()
        var common: CommonData? = null

        try {
            val result = ExcelLoader.loadAll()
            members = result.first
            common = result.second
            combo.items.addAll(members.map { it.name })
        } catch (e: Exception) {
            showError("Excel 読み込みエラー", "members.xlsx を読み込めませんでした。\n${e.message}")
        }

        // ViewModel
        val viewModel = PdfUpdateViewModel(
            PdfEditor(),
            PdfRepository(),
            PdfRenderManager()
        )

        // PDF 更新処理
        fun updatePdf() {
            val member =
                if (combo.value == header) null
                else members.firstOrNull { it.name == combo.value }

            viewModel.updatePdf(
                member = member,
                common = common!!,
                reason = reasonArea.text,
                date = applyDateInput.getDate()
            )
        }

        // ImageView と ViewModel のバインド
        imageView.imageProperty().bind(viewModel.currentImage)

        // 入力イベント
        combo.setOnAction { updatePdf() }
        applyDateInput.setOnChange { updatePdf() }
        reasonArea.textProperty().addListener { _, _, _ ->
            if (common == null) return@addListener
            updatePdf()
        }

        // 保存ボタン
        val exportButton = Button("保存").apply { prefWidth = 120.0 }
        exportButton.setOnAction {
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

        // 下部 UI
        val bottom = VBox(
            15.0,
            HBox(10.0, Label("利用者"), combo),
            HBox(10.0, Label("申請日"), applyDateInput.toHBox()),
            HBox(10.0, Label("変更申請理由"), reasonArea),
            HBox(10.0, exportButton)
        ).apply { style = "-fx-padding: 15px;" }

        root.bottom = bottom

        // シーン設定
        stage.scene = Scene(root, 900.0, 600.0)
        stage.title = "CareDoc"
        stage.show()

        // 起動時テンプレート読み込み
        try {
            val templateStream = javaClass.getResourceAsStream("/templates/template.pdf")
                ?: throw IllegalStateException("template.pdf が見つかりません")

            val tempFile = File.createTempFile("template", ".pdf")

            templateStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            viewModel.loadPdf(tempFile)

        } catch (e: Exception) {
            showError("テンプレート読込エラー", "template.pdf を読み込めませんでした。\n${e.message}")
        }
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

fun main() {
    Application.launch(PdfViewer::class.java)
}
