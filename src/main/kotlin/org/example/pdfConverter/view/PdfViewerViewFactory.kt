package org.example.pdfConverter.view

import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*

class PdfViewerViewFactory {

    fun createTitle(): Label =
        Label("介護認定申請書 作成アプリ CareDoc").apply {
            style = "-fx-font-size: 18px; -fx-padding: 10px;"
        }

    fun createPdfScrollPane(imageView: ImageView): ScrollPane =
        ScrollPane(StackPane(imageView)).apply {
            isPannable = true
        }

    fun createBottomPanel(
        combo: ComboBox<String>,
        applyDateBox: HBox,
        reasonArea: TextArea,
        exportButton: Button
    ): VBox =
        VBox(
            15.0,
            HBox(10.0, Label("利用者"), combo),
            HBox(10.0, Label("申請日"), applyDateBox),
            HBox(10.0, Label("変更申請理由"), reasonArea),
            HBox(10.0, exportButton)
        ).apply {
            style = "-fx-padding: 15px;"
        }
}
