package org.example.pdfconverter.viewModel

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import org.example.pdfconverter.model.CommonData
import org.example.pdfconverter.model.Member
import org.example.pdfconverter.render.PdfRenderManager
import org.example.pdfconverter.repository.PdfRepository
import org.example.pdfconverter.service.PdfEditor
import java.io.File

class PdfUpdateViewModel(
    private val pdfEditor: PdfEditor,
    private val pdfRepository: PdfRepository,
    private val renderManager: PdfRenderManager
) {

    val currentImage = SimpleObjectProperty<Image>()
    val currentPdfFile = SimpleObjectProperty<File?>()

    /**
     * PDF を編集して、レンダリングを依頼する
     */
    fun updatePdf(
        member: Member?,
        common: CommonData,
        reason: String,
        date: Triple<Int?, Int?, Int?>
    ) {
        val (year, month, day) = date

        val file = pdfEditor.editPdf(
            member = member,
            common = common,
            applyYear = year,
            applyMonth = month,
            applyDay = day,
            changeRequestReason = reason
        )

        // ▼ 非同期レンダリングへ
        loadPdfAsync(file)
    }

    /**
     * 同期読み込み（必要なら残す）
     */
    fun loadPdf(file: File) {
        currentPdfFile.set(file)
        currentImage.set(pdfRepository.loadFirstPage(file))
    }

    /**
     * PdfRenderManager に非同期レンダリングを依頼する
     */
    fun loadPdfAsync(file: File) {
        renderManager.loadPdfAsync(
            file = file,
            onSuccess = { image, newFile ->
                currentImage.set(image)
                currentPdfFile.set(newFile)
            },
            onError = { e ->
                e.printStackTrace()
            }
        )
    }

    fun exportPdfTo(target: File) {
        val src = currentPdfFile.get() ?: return
        src.copyTo(target, overwrite = true)
    }

}
