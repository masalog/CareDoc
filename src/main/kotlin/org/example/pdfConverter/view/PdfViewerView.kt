package org.example.pdfConverter.view

import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.viewModel.DateInputViewModel

class PdfViewerView(
    members: List<Member>
) : PdfViewerUI {

    // ------------------------------------------------------------
    //  UI コンポーネント（外に出さない！）
    // ------------------------------------------------------------
    private val combo = ComboBox<String>()
    private val reasonArea = TextArea()
    private val applyDateInput = DateInputViewModel()
    private val exportButton = Button("保存")

    val imageView = ImageView()
    val applyDateBox: HBox = applyDateInput.toHBox()

    val root: BorderPane

    init {
        val factory = PdfViewerViewFactory()

        // ▼ コンボボックス初期化
        combo.items.add("名前を選択してください")
        combo.items.addAll(members.map { it.name })
        combo.value = "名前を選択してください"

        // ▼ 理由入力欄
        reasonArea.promptText = "変更申請の理由を入力してください"
        reasonArea.prefRowCount = 2
        reasonArea.isWrapText = true

        // ▼ 画面構築
        root = BorderPane().apply {
            top = factory.createTitle()
            center = factory.createPdfScrollPane(imageView)
            bottom = factory.createBottomPanel(
                combo,
                applyDateBox,
                reasonArea,
                exportButton
            )
        }
    }

    // ------------------------------------------------------------
    // PdfViewerUI 実装（ここだけ外とつながる）
    // ------------------------------------------------------------

    override fun getSelectedName(): String? {
        return combo.value
    }

    override fun getReason(): String {
        return reasonArea.text
    }

    override fun getDate(): Triple<Int?, Int?, Int?> {
        return applyDateInput.getDate()
    }

    override fun setOnNameChanged(handler: () -> Unit) {
        combo.setOnAction { handler() }
    }

    override fun setOnReasonChanged(handler: () -> Unit) {
        reasonArea.textProperty().addListener { _, _, _ ->
            handler()
        }
    }

    override fun setOnDateChanged(handler: () -> Unit) {
        applyDateInput.setOnChange {
            handler()
        }
    }

    override fun setOnExportClicked(handler: () -> Unit) {
        exportButton.setOnAction {
            handler()
        }
    }
}