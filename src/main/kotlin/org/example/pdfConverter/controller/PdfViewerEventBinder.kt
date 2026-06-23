package org.example.pdfConverter.controller

import javafx.stage.FileChooser
import javafx.stage.Stage
import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.view.PdfViewerUI
import org.example.pdfConverter.viewModel.PdfUpdateViewModel

class PdfViewerEventBinder(
    private val view: PdfViewerUI,
    private val viewModel: PdfUpdateViewModel,
    private val members: List<Member>,
    private val common: CommonData?,
    private val stage: Stage,
    private val errorHandler: ErrorHandler
) {

    fun bind() {

        fun updatePdf() {
            val loadedCommon = common ?: return

            val selectedMember =
                members.firstOrNull { it.name == view.getSelectedName() }

            viewModel.updatePdf(
                member = selectedMember,
                common = loadedCommon,
                reason = view.getReason(),
                date = view.getDate()
            )
        }

        view.setOnNameChanged { updatePdf() }
        view.setOnDateChanged { updatePdf() }
        view.setOnReasonChanged { updatePdf() }

        view.setOnExportClicked {
            val chooser = FileChooser().apply {
                title = "保存先を選択"
                initialFileName = "output.pdf"
                extensionFilters.add(
                    FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                )
            }

            val saveFile = chooser.showSaveDialog(stage) ?: return@setOnExportClicked

            try {
                viewModel.exportPdfTo(saveFile)
            } catch (e: Exception) {
                errorHandler.showError("PDF エクスポートエラー", e)
            }
        }
    }
}