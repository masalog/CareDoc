package org.example.pdfConverter.model

data class PdfLayout(
    var fields: Map<String, FieldPosition> = emptyMap()
)