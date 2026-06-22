package org.example.pdfConverter.service

import javafx.application.Platform
import javafx.scene.control.Alert

class ErrorHandlerImpl : ErrorHandler {
    override fun showError(message: String?) {
        Platform.runLater {
            Alert(Alert.AlertType.ERROR).apply {
                title = "初期データ読込エラー"
                headerText = null
                contentText = message ?: "不明なエラーが発生しました"
            }.showAndWait()
        }
    }
}
