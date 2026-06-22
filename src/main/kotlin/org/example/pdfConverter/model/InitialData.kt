package org.example.pdfConverter.model

import java.io.File

/**
 * PDF Viewer 初期化時に必要なデータをまとめた DTO。
 */
data class InitialData(
    val members: List<Member>,
    val common: CommonData?,
    val templatePdf: File
)
