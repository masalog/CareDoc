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

    // ▼ PDF レンダリングを直列化する Executor
    private val renderExecutor = Executors.newSingleThreadExecutor()

    // ▼ 最新ジョブのみ反映するためのシーケンス番号
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

        // -----------------------------
        // Excel 読み込み（例外を UI に通知）
        // -----------------------------
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

            currentPdfFile = file
            loadPdfAsync(file)
        }

        combo.setOnAction { updatePdf() }
        applyDateInput.setOnChange { updatePdf() }

        val exportButton = Button("保存").apply { prefWidth = 120.0 }
        exportButton.setOnAction {
            println("=== 保存ボタン押下 ===")
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

        // -----------------------------
        // テンプレート読み込み（例外を UI に通知）
        // -----------------------------
        try {
            val templateStream = javaClass.getResourceAsStream("/templates/template.pdf")
                ?: throw IllegalStateException("template.pdf が見つかりません")

            val tempFile = File.createTempFile("template", ".pdf").apply { deleteOnExit() }

            templateStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            println("テンプレート読込成功: ${tempFile.absolutePath} サイズ=${tempFile.length()} bytes")

            currentPdfFile = tempFile
            loadPdfAsync(tempFile)

        } catch (e: Exception) {
            showError("テンプレート読込エラー", "template.pdf を読み込めませんでした。\n${e.message}")
        }
    }

    // ============================================
    // ▼ PDF レンダリング（直列化 & 最新のみ反映）
    // ============================================
    private fun loadPdfAsync(file: File) {
        val seq = ++renderSeq  // このジョブの番号

        renderExecutor.submit {
            println("PDF読み込み開始: ${file.absolutePath}")

            runCatching {
                PDDocument.load(file).use { doc ->
                    PDFRenderer(doc).renderImageWithDPI(0, 150f)
                }
            }.onSuccess { image ->
                Platform.runLater {
                    // 最新ジョブのみ反映
                    if (seq == renderSeq) {
                        println("PDF表示更新（seq=$seq）")
                        imageView.image = SwingFXUtils.toFXImage(image, null)
                    } else {
                        println("古いジョブのため破棄（seq=$seq）")
                    }
                }
            }.onFailure { e ->
                e.printStackTrace()
                showError("PDF 読み込みエラー", e.message ?: "不明なエラー")
            }
        }
    }

    private fun exportPdf(stage: Stage, file: File) {

        println("=== exportPdf() 開始 ===")
        println("FXスレッド？ = ${Platform.isFxApplicationThread()}")
        println("stage.isShowing = ${stage.isShowing}")

        val chooser = FileChooser().apply {
            title = "保存先を選択"

            val home = File(System.getProperty("user.home"))
            if (home.exists()) {
                initialDirectory = home
            }

            initialFileName = "output.pdf"
            extensionFilters.add(FileChooser.ExtensionFilter("PDF Files", "*.pdf"))
        }

        val saveFile = chooser.showSaveDialog(stage)

        println("ユーザーが選んだ保存先 = $saveFile")

        if (saveFile == null) {
            println("保存キャンセル（または表示失敗）")
            return
        }

        try {
            file.copyTo(saveFile, overwrite = true)
            println("コピー成功！")
        } catch (e: Exception) {
            e.printStackTrace()
            showError(
                "PDF の保存に失敗しました",
                "ファイルを保存できませんでした。\n原因: ${e.message}"
            )
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
