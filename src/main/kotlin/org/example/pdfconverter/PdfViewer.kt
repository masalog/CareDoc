package org.example.pdfconverter

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File
import java.io.InputStream

class PdfViewer : Application() {

    private var pdf: PDDocument? = null
    private lateinit var imageView: ImageView
    private var currentPdfFile: File = File("edited.pdf")

    override fun start(stage: Stage) {

        val root = BorderPane()

        // ======================
        // ▼ PDF表示エリア（スクロール対応）
        // ======================
        imageView = ImageView()
        val scrollPane = ScrollPane(imageView).apply {
            isPannable = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            isFitToWidth = false
            isFitToHeight = false
        }
        root.center = scrollPane

        // ======================
        // ▼ 下部 UI（プルダウン + 出力ボタン）
        // ======================
        val combo = ComboBox<String>()
        val header = "選択肢"

        combo.items.add(header)
        combo.items.addAll("選択肢A", "選択肢B", "選択肢C")
        combo.value = header

        combo.setOnAction {
            val selected = combo.value
            if (selected == header) {
                combo.value = header
                return@setOnAction
            }

            val editedFile = editPdf(selected)
            currentPdfFile = editedFile
            loadPdf(editedFile)
        }

        val exportButton = Button("出力")
        exportButton.setOnAction {
            if (exportPdf(stage)) {
                stage.close()
            }
        }

        root.bottom = HBox(10.0, combo, exportButton)

        // ======================
        // ▼ ウィンドウ設定
        // ======================
        stage.scene = Scene(root, 800.0, 500.0)
        stage.title = "PDF Viewer"
        stage.show()

        // 起動時にテンプレート表示
        val template = extractTemplateToTempFile()
        currentPdfFile = template
        loadPdf(template)

        stage.setOnCloseRequest {
            pdf?.close()
        }
    }

    // ======================
    // ▼ JAR 内リソースを InputStream で取得
    // ======================
    private fun getTemplateStream(): InputStream =
        PdfViewer::class.java.getResourceAsStream("/templates/template.pdf")
            ?: throw IllegalStateException("PDF が見つかりません: /templates/template.pdf")

    private fun getFontStream(): InputStream =
        PdfViewer::class.java.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf")
            ?: throw IllegalStateException("フォントが見つかりません: /fonts/NotoSansJP-Regular.ttf")

    // ======================
    // ▼ JAR 内リソースは File にできないため、一時ファイルに展開
    // ======================
    private fun extractTemplateToTempFile(): File {
        val temp = File.createTempFile("template", ".pdf")
        temp.outputStream().use { out ->
            getTemplateStream().use { input ->
                input.copyTo(out)
            }
        }
        return temp
    }

    // ======================
    // ▼ PDF 読み込み（例外処理付き）
    // ======================
    private fun loadPdf(file: File) {
        try {
            pdf?.close()
            pdf = PDDocument.load(file)

            val renderer = PDFRenderer(pdf)
            val image = renderer.renderImageWithDPI(0, 150f)

            val fxImage = SwingFXUtils.toFXImage(image, null)
            imageView.image = fxImage

        } catch (e: Exception) {
            e.printStackTrace()
            showError("PDF の読み込みに失敗しました。\nファイルが壊れている可能性があります。")
        }
    }

    // ======================
    // ▼ PDF 編集処理
    // ======================
    private fun editPdf(selectedText: String): File {

        val outputFile = File("edited.pdf")

        getTemplateStream().use { input ->
            PDDocument.load(input).use { document ->

                val page = document.getPage(0)
                val font = PDType0Font.load(document, getFontStream())

                PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true
                ).use { content ->

                    content.beginText()
                    content.setFont(font, 16f)
                    content.newLineAtOffset(150f, 450f)
                    content.showText(selectedText)
                    content.endText()
                }

                document.save(outputFile)
            }
        }

        return outputFile
    }

    // ======================
    // ▼ PDF 出力処理（例外処理付き）
    // ======================
    private fun exportPdf(stage: Stage): Boolean {

        val chooser = FileChooser().apply {
            title = "PDF を保存"
            extensionFilters.add(FileChooser.ExtensionFilter("PDF Files", "*.pdf"))
            initialFileName = "output.pdf"
        }

        val saveFile = chooser.showSaveDialog(stage) ?: return false

        return try {
            currentPdfFile.copyTo(saveFile, overwrite = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            showError("PDF の保存に失敗しました。\n保存先に書き込みできない可能性があります。")
            false
        }
    }
    }
}

fun main() {
    Application.launch(PdfViewer::class.java)
}
