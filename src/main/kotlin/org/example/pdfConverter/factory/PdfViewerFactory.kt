package org.example.pdfConverter.factory

import org.example.pdfConverter.model.InitialData
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.service.PdfEditor
import org.example.pdfConverter.repository.PdfRepository
import org.example.pdfConverter.render.PdfRenderManager

/**
 * PdfViewer の View と ViewModel をまとめて生成する Factory クラス。
 * Controller の肥大化を防ぐために導入。
 */
class PdfViewerFactory {

    /**
     * 初期データを元に ViewModel と View を生成し、PDF の初期表示まで行う。
     *
     * @param initialData 初期データ（メンバー一覧、共通データ、テンプレ PDF）
     * @return Pair<ViewModel, View>
     */
    fun create(initialData: InitialData): Pair<PdfUpdateViewModel, PdfViewerView> {

        // ViewModel 生成
        val viewModel = PdfUpdateViewModel(
            PdfEditor(),
            PdfRepository(),
            PdfRenderManager()
        )

        // View 生成
        val view = PdfViewerView(initialData.members)

        // PDF 初期ロード
        viewModel.loadPdf(initialData.templatePdf)

        // View と ViewModel のバインド
        view.imageView.imageProperty().bind(viewModel.currentImage)

        return viewModel to view
    }
}
