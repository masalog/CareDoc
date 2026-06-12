package org.example.caredoc.pdf

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer

fun loadPdfPageAsImage(resourcePath: String, pageIndex: Int): ImageBitmap {

    val pdfStream = {}.javaClass.getResourceAsStream(resourcePath)
        ?: throw IllegalArgumentException("PDF が見つかりません: $resourcePath")

    PDDocument.load(pdfStream).use { pdf ->

        val renderer = PDFRenderer(pdf)

        // ★ 高画質設定（サブサンプリング無効）
        renderer.setSubsamplingAllowed(false)

        // ★ DPI ではなくスケールで描画（最強画質）
        //    3.0f〜4.0f が実用的
        val scale = 4.0f
        val awtImage = renderer.renderImage(pageIndex, scale)

        return awtImage.toComposeImageBitmap()
    }
}
