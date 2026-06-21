package org.example.pdfConverter.service

import java.io.File

class PdfLoader {

    fun loadTemplatePdf(): File {
        val stream = javaClass.getResourceAsStream("/templates/template.pdf")
            ?: throw IllegalStateException("template.pdf が見つかりません")

        val temp = File.createTempFile("template", ".pdf")

        stream.use { input ->
            temp.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return temp
    }
}
