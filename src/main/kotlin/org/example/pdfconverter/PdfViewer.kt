package org.example.pdfconverter

import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File
import java.util.concurrent.Executors

class PdfViewer : Application() {

    private lateinit var imageView: ImageView
    private var currentPdfFile: File? = null

    private val pdfEditor = PdfEditor()

    private val renderExecutor = Executors.newSingleThreadExecutor()
    @Volatile private var renderSeq: Long = 0

    override fun start(stage: Stage) {

        val root = BorderPane()

        val title = Label("介護認定申請書 作成アプリ CareDoc")
        title.style = "-fx-font-size: 18px; -fx-padding: 10px;"
        root.top = title

        imageView = ImageView()
        val stack = StackPane(imageView)
        val scrollPane = ScrollPane(stack).apply { isPannable = true }
        root.center = scrollPane

        val combo = ComboBox<String>()
        val header = "名前を選択してください"
        combo.items.add(header)
        combo.prefWidth = 250.0
        combo.value = header

        val applyDateInput = DateInputView()

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

        fun updatePdf() {
            val selected = combo.value
            if (selected == header) return
            if (common == null) return

            val member = members.firstOrNull { it.name == selected } ?: return
            val (year, month, day) = applyDateInput.getDate()

            val file = pdfEditor.editPdf(
                member = member,
                common = common!!,
                applyYear = year,
                applyMonth = month,
                applyDay = day
            )

            println("PDF更新: ${file.absolutePath} サイズ=${file.length()} bytes")

            loadPdfAsync(file)
        }

        combo.setOnAction { updatePdf() }
        applyDateInput.setOnChange { updatePdf() }

        val exportButton = Button("保存").apply { prefWidth = 120.0 }
        exportButton.setOnAction {
            currentPdfFile?.let { exportPdf(stage, it) }
                ?: println("currentPdfFile が null のため保存できません")
        }

        val bottom = VBox(
            15.0,
            HBox(10.0, Label("利用者"), combo),
            HBox(10.0, Label("申請日"), applyDateInput.toHBox()),
            HBox(10.0, exportButton)
        ).apply { style = "-fx-padding: 15px;" }

        root.bottom = bottom

        stage.scene = Scene(root, 900.0, 600.0)
        stage.title = "CareDoc"
        stage.show()

        try {
            val templateStream = javaClass.getResourceAsStream("/templates/template.pdf")
                ?: throw IllegalStateException("template.pdf が見つかりません")

            val tempFile = File.createTempFile("template", ".pdf")

            templateStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            println("テンプレート読込成功: ${tempFile.absolutePath}")

            loadPdfAsync(tempFile)

        } catch (e: Exception) {
            showError("テンプレート読込エラー", "template.pdf を読み込めませんでした。\n${e.message}")
        }
    }

    // ============================================
    // ▼ PDF レンダリング（直列化 & 最新のみ反映 & 古いPDF削除）
    // ============================================

    // ▼ 現在表示中の PDF（FX スレッドのみでアクセス）
    private var displayedPdfFile: File? = null

    private fun loadPdfAsync(file: File) {
        val seq = ++renderSeq

        renderExecutor.submit {
            println("PDF読み込み開始: ${file.absolutePath}")

            runCatching {
                PDDocument.load(file).use { doc ->
                    PDFRenderer(doc).renderImageWithDPI(0, 150f)
                }
            }.onSuccess { image ->
                Platform.runLater {
                    if (seq == renderSeq) {

                        // ▼ 前回の表示PDFを削除
                        val old = displayedPdfFile
                        imageView.image = SwingFXUtils.toFXImage(image, null)
                        displayedPdfFile = file

                        old?.takeIf { it.exists() && it != file }?.delete()

                        // ▼ 保存ボタン用の currentPdfFile は「表示された PDF」に更新
                        currentPdfFile = file

                        println("PDF表示更新（seq=$seq）")
                    } else {
                        // ▼ 古いジョブの PDF は削除
                        file.delete()
                        println("古いジョブのため破棄・削除（seq=$seq）")
                    }
                }
            }.onFailure { e ->
                e.printStackTrace()

                Platform.runLater {
                    // ▼ 失敗した PDF は削除
                    file.takeIf { it.exists() && it != displayedPdfFile }?.delete()

                    showError("PDF 読み込みエラー", e.message ?: "不明なエラー")
                }
            }
        }
    }

    private fun exportPdf(stage: Stage, file: File) {
        val chooser = FileChooser().apply {
            title = "保存先を選択"
            initialFileName = "output.pdf"
            extensionFilters.add(FileChooser.ExtensionFilter("PDF Files", "*.pdf"))
        }

        val saveFile = chooser.showSaveDialog(stage) ?: return

        try {
            file.copyTo(saveFile, overwrite = true)
        } catch (e: Exception) {
            showError("PDF の保存に失敗しました", "原因: ${e.message}")
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
