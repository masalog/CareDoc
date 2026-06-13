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

        // ▼ 最初の見出し（選択不可）
        val header = "選択肢"
        combo.items.add(header)

        // ▼ 実際の選択肢
        combo.items.addAll("選択肢A", "選択肢B", "選択肢C")

        // ▼ 初期表示は「選択肢」
        combo.value = header

        // ▼ 選択不可ロジック
        combo.setOnAction {
            val selected = combo.value

            if (selected == header) {
                // 見出しを選ばせない
                combo.value = header
                return@setOnAction
            }

            // ▼ 有効な選択肢なら PDF を編集
            val editedFile = editPdf(selected)
            currentPdfFile = editedFile
            loadPdf(editedFile)
        }

        // ▼ 出力ボタン
        val exportButton = Button("出力")
        exportButton.setOnAction {
            if (exportPdf(stage)) {
                stage.close()   // 保存成功後にウィンドウを閉じる
            }
        }

        val bottomBox = HBox(10.0, combo, exportButton)
        root.bottom = bottomBox

        // ======================
        // ▼ ウィンドウ設定
        // ======================
        stage.scene = Scene(root, 800.0, 500.0)
        stage.title = "PDF Viewer"
        stage.show()

        // 起動時に元PDFを表示
        val template = getTemplateFile()
        currentPdfFile = template
        loadPdf(template)

        stage.setOnCloseRequest {
            pdf?.close()
        }
    }

    private fun getTemplateFile(): File {
        val url = PdfViewer::class.java.getResource("/templates/template.pdf")
            ?: throw IllegalStateException("PDF が見つかりません: /templates/template.pdf")
        return File(url.toURI())
    }

    private fun getFontFile(): File {
        val url = PdfViewer::class.java.getResource("/fonts/NotoSansJP-Regular.ttf")
            ?: throw IllegalStateException("フォントが見つかりません: /fonts/NotoSansJP-Regular.ttf")
        return File(url.toURI())
    }

    private fun loadPdf(file: File) {

        pdf?.close()
        pdf = PDDocument.load(file)

        val renderer = PDFRenderer(pdf)
        val image = renderer.renderImageWithDPI(0, 150f)

        val fxImage = SwingFXUtils.toFXImage(image, null)
        imageView.image = fxImage
    }

    /**
     * ▼ PDF を編集して保存
     */
    private fun editPdf(selectedText: String): File {

        val inputFile = getTemplateFile()
        val outputFile = File("edited.pdf")

        PDDocument.load(inputFile).use { document ->

            val page = document.getPage(0)
            val font = PDType0Font.load(document, getFontFile())

            PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true
            ).use { content ->

                content.beginText()
                content.setFont(font, 16f)

                // ▼ 特定座標に書き込む
                content.newLineAtOffset(150f, 450f)
                content.showText(selectedText)

                content.endText()
            }

            document.save(outputFile)
        }

        return outputFile
    }

    /**
     * ▼ PDF 出力処理
     */
    private fun exportPdf(stage: Stage): Boolean {

        val chooser = FileChooser()
        chooser.title = "PDF を保存"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("PDF Files", "*.pdf"))
        chooser.initialFileName = "output.pdf"

        val saveFile = chooser.showSaveDialog(stage) ?: return false

        currentPdfFile.copyTo(saveFile, overwrite = true)
        return true
    }
}

fun main() {
    Application.launch(PdfViewer::class.java)
}
