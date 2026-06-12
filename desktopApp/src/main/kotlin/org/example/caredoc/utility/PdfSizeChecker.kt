package org.example.caredoc.utility

import org.apache.pdfbox.pdmodel.PDDocument

// PDF のページサイズ（pt）を調べるためだけのユーティリティ
object PdfSizeChecker {

    @JvmStatic
    fun main(args: Array<String>) {

        // resources/templates フォルダから PDF を読み込む
        // classLoader を使うことで、Jar 化しても正しく読み込める
        val pdfStream = PdfSizeChecker::class.java.classLoader
            .getResourceAsStream("templates/template.pdf")
            ?: error("PDF が見つかりません")

        // PDF をロード
        val document = PDDocument.load(pdfStream)

        // 1ページ目を取得（0 が最初のページ）
        val page = document.getPage(0)

        // PDF の幅・高さを pt（ポイント）単位で取得
        // A4 の場合は width=595, height=842 が一般的
        println("PDF width = ${page.mediaBox.width}")
        println("PDF height = ${page.mediaBox.height}")

        // PDF を閉じる（メモリ解放）
        document.close()
    }
}