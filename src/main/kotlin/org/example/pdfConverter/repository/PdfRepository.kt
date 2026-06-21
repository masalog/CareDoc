package org.example.pdfConverter.repository

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File

class PdfRepository {

    fun loadFirstPage(file: File, dpi: Float = 150f): Image {
        PDDocument.load(file).use { doc ->
            val renderer = PDFRenderer(doc)
            val buffered = renderer.renderImageWithDPI(0, dpi)  // ← BufferedImage
            return SwingFXUtils.toFXImage(buffered, null)       // ← Image に変換
        }
    }
}