package org.example.pdfConverter.service

import javafx.application.Platform
import javafx.scene.control.Alert

class ErrorHandlerImpl : ErrorHandler {

    override fun showError(title: String, message: String?) {
        Platform.runLater {
            Alert(Alert.AlertType.ERROR).apply {
                this.title = title
                headerText = null
                contentText = message ?: "不明なエラーが発生しました"
            }.showAndWait()
        }
    }
}
