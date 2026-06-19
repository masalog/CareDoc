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

        stage.setOnCloseRequest { pdf?.close() }
    }

    // ======================
    // ▼ リソース取得
    // ======================
    private fun getTemplateStream(): InputStream =
        PdfViewer::class.java.getResourceAsStream("/templates/template.pdf")
            ?: throw IllegalStateException("PDF が見つかりません")

    private fun getFontStream(): InputStream =
        PdfViewer::class.java.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf")
            ?: throw IllegalStateException("フォントが見つかりません")

    // ======================
    // ▼ テンプレート展開
    // ======================
    private fun extractTemplateToTempFile(): File {
        val temp = File.createTempFile("template", ".pdf")
        temp.outputStream().use { out ->
            getTemplateStream().use { input -> input.copyTo(out) }
        }
        return temp
    }

    // ======================
    // ▼ PDF 読み込み
    // ======================
    private fun loadPdf(file: File) {
        try {
            pdf?.close()

            pdf = PDDocument.load(file)
            val renderer = PDFRenderer(pdf)
            val image = renderer.renderImageWithDPI(0, 150f)

            imageView.image = SwingFXUtils.toFXImage(image, null)

        } catch (e: IOException) {
            e.printStackTrace()
            showError("PDF の読み込みに失敗しました: ${e.message}")

        } catch (e: Exception) {
            e.printStackTrace()
            showError("予期しないエラーが発生しました: ${e.message}")
        }
    }


    // ======================
    // ▼ PDF 編集処理（Member + CommonData）
    // ======================
    private fun editPdf(member: Member, common: CommonData): File {

        val outputFile = File("edited.pdf")
        val layout = LayoutLoader.loadLayout()

        // ▼ 個別 + 共通データ
        val values =
            mapOf(
                "name" to member.name,
                "furigana" to member.furigana,
                "birthYear" to member.birthYear.toString(),
                "birthMonth" to member.birthMonth.toString(),
                "birthDay" to member.birthDay.toString(),
                "gender" to member.gender,
                "address" to member.address,
                "phone" to member.phone,
                "Insurance ID Number" to member.insuranceIdNumber
            ) + mapOf(
                "facilityName" to common.facilityName,
                "facilityPhone" to common.facilityPhone,
                "institutionName" to common.institutionName,
                "institutionAddress" to common.institutionAddress,
                "agentName" to common.agentName,
                "agentPostal" to common.agentPostal,
                "agentAddress" to common.agentAddress,
                "agentPhone" to common.agentPhone,
                "doctorName" to common.doctorName,
                "clinicName" to common.clinicName,
                "clinicPostal" to common.clinicPostal,
                "clinicAddress" to common.clinicAddress,
                "clinicPhone" to common.clinicPhone
            )

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

                    // ============================
                    // ▼ editPdf() 内の共通関数
                    // ============================
                    fun drawText(key: String, value: String) {
                        layout.fields[key]?.let { pos ->
                            content.beginText()
                            content.setFont(font, pos.fontSize)
                            content.newLineAtOffset(pos.x, pos.y)
                            content.showText(value)
                            content.endText()
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
