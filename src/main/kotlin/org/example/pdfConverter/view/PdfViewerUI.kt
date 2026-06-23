package org.example.pdfConverter.view

interface PdfViewerUI {
    fun getSelectedName(): String?
    fun getReason(): String
    fun getDate(): Triple<Int?, Int?, Int?>
    fun setOnNameChanged(handler: () -> Unit)
    fun setOnReasonChanged(handler: () -> Unit)
    fun setOnDateChanged(handler: () -> Unit)
    fun setOnExportClicked(handler: () -> Unit)
}
