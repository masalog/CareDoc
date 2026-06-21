package org.example.pdfConverter.service

import org.example.pdfConverter.model.CommonData
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.repository.ExcelLoader
import java.io.File

class PdfViewerInitializer(
    private val excelLoader: ExcelLoader = ExcelLoader,
    private val pdfLoader: PdfLoader = PdfLoader()
) {

    fun loadInitialData(): InitialData {
        val (members, common) = excelLoader.loadAll()
        val templatePdf = pdfLoader.loadTemplatePdf()

        return InitialData(
            members = members,
            common = common,
            templatePdf = templatePdf
        )
    }
}

data class InitialData(
    val members: List<Member>,
    val common: CommonData,
    val templatePdf: File
)