package org.example.pdfConverter.render

import javafx.application.Platform
import javafx.scene.image.Image
import org.example.pdfConverter.repository.PdfRepository
import java.io.File

class PdfRenderManager : AutoCloseable {

    private val pdfRepository = PdfRepository()
    private val jobManager = RenderJobManager()
    private val executor = RenderExecutor()
    private val displayController = PdfDisplayController()

    fun loadPdfAsync(
        file: File,
        onSuccess: (Image, File) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val seq = jobManager.nextSeq()

        executor.submit {
            println("PDF読み込み開始: ${file.absolutePath}")

            runCatching {
                pdfRepository.loadFirstPage(file)
            }.onSuccess { image ->
                Platform.runLater {
                    if (jobManager.isLatest(seq)) {
                        println("PDF表示更新（seq=$seq）")
                        displayController.updateDisplay(image, file, onSuccess)
                    } else {
                        println("古いジョブのため破棄・削除（seq=$seq）")
                        file.delete()
                    }
                }
            }.onFailure { e ->
                e.printStackTrace()
                Platform.runLater {
                    displayController.handleError(file, onError, e)
                }
            }
        }
    }

    override fun close() {
        executor.close()
    }
}
