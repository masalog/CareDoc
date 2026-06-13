package org.example.pdfconverter.utility

/**
 * 画像座標(px) → PDF座標(pt) に変換するユーティリティクラス
 *
 * @param pdfWidth PDF の幅（pt）
 * @param pdfHeight PDF の高さ（pt）
 * @param imageWidth 画像の幅（px）
 * @param imageHeight 画像の高さ（px）
 */
class PdfPositionConverter(
    private val pdfWidth: Float,
    private val pdfHeight: Float,
    private val imageWidth: Int,
    private val imageHeight: Int
) {

    /** X座標(px) → PDF X座標(pt) */
    fun toPdfX(imageX: Int): Float =
        imageX * (pdfWidth / imageWidth)

    /** Y座標(px) → PDF Y座標(pt)（PDF は左下が原点なので上下反転） */
    fun toPdfY(imageY: Int): Float =
        (imageHeight - imageY) * (pdfHeight / imageHeight)

    /** (x, y) のセットを PDF 座標に変換 */
    fun toPdfPoint(imageX: Int, imageY: Int): Pair<Float, Float> =
        Pair(toPdfX(imageX), toPdfY(imageY))
}

/**
 * 動作確認用の main()（標準出力）
 */
fun main() {

    // PDF と画像のサイズ（あなたの環境に合わせた値）
    val converter = PdfPositionConverter(
        pdfWidth = 595.32f,
        pdfHeight = 842.04f,
        imageWidth = 1241,
        imageHeight = 1755
    )

    // テスト用の画像座標（例：x=300, y=500）
    val imageX = 872
    val imageY = 256

    val (pdfX, pdfY) = converter.toPdfPoint(imageX, imageY)

    println("PDF X = $pdfX")
    println("PDF Y = $pdfY")
}
