package org.example.pdfConverter.service

interface ErrorHandler {
    fun showError(title: String, message: String?)
}
