package org.example.pdfConverter.render

import javafx.scene.image.Image
import java.io.File

class PdfDisplayController {

    private var displayedPdfFile: File? = null

    fun updateDisplay(image: Image, file: File, onSuccess: (Image, File) -> Unit) {
        val old = displayedPdfFile
        displayedPdfFile = file

        old?.takeIf { it.exists() && it != file }?.delete()

        onSuccess(image, file)
    }

    fun handleError(file: File, displayed: File?, onError: (Throwable) -> Unit, e: Throwable) {
        file.takeIf { it.exists() && it != displayed }?.delete()
        onError(e)
    }
}
