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
) {
    val imageView = ImageView()
    val combo = ComboBox<String>()
    val reasonArea = TextArea()
    val applyDateInput = DateInputViewModel()
    val applyDateBox: HBox = applyDateInput.toHBox()
    val exportButton = Button("保存")

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
}
