package org.example.pdfConverter.controller

import javafx.stage.FileChooser
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel

class PdfViewerEventBinder(
    private val view: PdfViewerView,
    private val viewModel: PdfUpdateViewModel,
    private val members: List<Member>,
    private val common: CommonData?,
    private val stage: Stage,
    private val errorHandler: ErrorHandler   // ★ 追加
) {

    fun bind() {

        fun updatePdf() {
            val loadedCommon = common ?: return

            val selectedMember =
                members.firstOrNull { it.name == view.combo.value }

            viewModel.updatePdf(
                member = selectedMember,
                common = loadedCommon,
                reason = view.reasonArea.text,
                date = view.applyDateInput.getDate()
            )
        }

        view.combo.setOnAction { updatePdf() }
        view.applyDateInput.setOnChange { updatePdf() }
        view.reasonArea.textProperty().addListener { _, _, _ -> updatePdf() }

        view.exportButton.setOnAction {
            val chooser = FileChooser().apply {
                title = "保存先を選択"
                initialFileName = "output.pdf"
                extensionFilters.add(
                    FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                )
            }

            val saveFile = chooser.showSaveDialog(stage) ?: return@setOnAction

            try {
                viewModel.exportPdfTo(saveFile)
            } catch (e: Exception) {
                errorHandler.showError("PDF エクスポートエラー", e.message)
            }
        }
    }
}
