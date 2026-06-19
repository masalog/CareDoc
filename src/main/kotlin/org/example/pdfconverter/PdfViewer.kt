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

class PdfViewer : Application() {

    private lateinit var imageView: ImageView
    private var currentPdfFile: File? = null

    private val pdfEditor = PdfEditor()

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

        val (members, common) = ExcelLoader.loadAll()
        combo.items.addAll(members.map { it.name })

        fun updatePdf() {
            val selected = combo.value
            if (selected == header) return

            val member = members.firstOrNull { it.name == selected } ?: return
            val (year, month, day) = applyDateInput.getDate()

            val file = pdfEditor.editPdf(
                member = member,
                common = common,
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

        val templateStream = javaClass.getResourceAsStream("/templates/template.pdf")
            ?: error("template.pdf が見つかりません")

        val tempFile = File.createTempFile("template", ".pdf")

        templateStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        println("テンプレート読込成功: ${tempFile.absolutePath} サイズ=${tempFile.length()} bytes")

        currentPdfFile = tempFile
        loadPdfAsync(tempFile)
    }

    private fun loadPdfAsync(file: File) {
        Thread {
            println("PDF読み込み開始: ${file.absolutePath}")

            val doc = PDDocument.load(file)
            val image = PDFRenderer(doc).renderImageWithDPI(0, 150f)
            doc.close()

            Platform.runLater {
                println("PDF表示更新")
                imageView.image = SwingFXUtils.toFXImage(image, null)
            }
        }.start()
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

            initialFileName = "output.pdf" // ✅ 追加
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
        }
    }

}

fun main() {
    Application.launch(PdfViewer::class.java)
}
