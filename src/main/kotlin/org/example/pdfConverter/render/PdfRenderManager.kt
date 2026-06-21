package org.example.pdfConverter.render

import javafx.application.Platform
import javafx.scene.image.Image
import org.example.pdfConverter.repository.PdfRepository
import java.io.File
import java.util.concurrent.Executors

class PdfRenderManager {

    // ▼ PDF 読み込み用 Repository
    private val pdfRepository = PdfRepository()

    // ▼ 直列実行のための Executor
    private val renderExecutor = Executors.newSingleThreadExecutor()

    // ▼ 最新ジョブのみ反映するためのシーケンス番号
    @Volatile
    private var renderSeq: Long = 0

    // ▼ 現在表示中の PDF（FX スレッドのみでアクセス）
    private var displayedPdfFile: File? = null

    /**
     * PDF を非同期で読み込み、画像化し、最新ジョブのみ UI に反映する
     */
    fun loadPdfAsync(
        file: File,
        onSuccess: (Image, File) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val seq = ++renderSeq

        renderExecutor.submit {
            println("PDF読み込み開始: ${file.absolutePath}")

            runCatching {
                pdfRepository.loadFirstPage(file)
            }.onSuccess { image ->
                Platform.runLater {
                    if (seq == renderSeq) {

                        // ▼ 前回の表示PDFを削除
                        val old = displayedPdfFile
                        displayedPdfFile = file
                        old?.takeIf { it.exists() && it != file }?.delete()

                        println("PDF表示更新（seq=$seq）")

                        // ▼ 呼び出し元（ViewModel）に通知
                        onSuccess(image, file)

                    } else {
                        // ▼ 古いジョブの PDF は削除
                        file.delete()
                        println("古いジョブのため破棄・削除（seq=$seq）")
                    }
                }
            }.onFailure { e ->
                e.printStackTrace()

                Platform.runLater {
                    // ▼ 失敗した PDF は削除
                    file.takeIf { it.exists() && it != displayedPdfFile }?.delete()

                    onError(e)
                }
            }
        }
    }
}
