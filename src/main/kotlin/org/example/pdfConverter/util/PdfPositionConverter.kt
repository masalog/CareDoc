package org.example.pdfConverter.util

import org.yaml.snakeyaml.Yaml
import java.io.File
import org.example.pdfConverter.model.PdfLayout
import org.example.pdfConverter.model.FieldPosition
import java.nio.file.Files

// ==============================
// ▼ データクラス
// ==============================

data class ConvertedFieldPosition(
    val x: Float,
    val y: Float,
    val fontSize: Int
)

data class ConvertedLayout(
    val fields: Map<String, ConvertedFieldPosition>
)

// ==============================
// ▼ 座標変換クラス
// ==============================
class PdfPositionConverter(
    private val pdfWidth: Float,
    private val pdfHeight: Float,
    private val imageWidth: Int,
    private val imageHeight: Int
) {

    fun toPdfX(imageX: Int): Float =
        imageX * (pdfWidth / imageWidth)

    fun toPdfY(imageY: Int): Float =
        (imageHeight - imageY) * (pdfHeight / imageHeight)

    fun toPdfPoint(imageX: Int, imageY: Int): Pair<Float, Float> =
        Pair(toPdfX(imageX), toPdfY(imageY))
}

// ==============================
// ▼ YAML 読み込み（fontSize 付き）
// ==============================
fun loadRawLayout(path: String): PdfLayout {

    // --- 許可する基準ディレクトリ ---
    val baseDir = File("src/main/resources/positions").canonicalFile

    // --- 入力パスを baseDir 起点で解決（これが最重要） ---
    val file = File(baseDir, path).canonicalFile

    // --- baseDir 配下にあるかチェック（パストラバーサル対策） ---
    if (file != baseDir && !file.path.startsWith(baseDir.path + File.separator)) {
        throw IllegalArgumentException("許可されていないディレクトリへのアクセスです: $path")
    }

    // --- 拡張子ホワイトリストチェック ---
    val allowedExtensions = setOf("yaml", "yml")
    val ext = file.extension.lowercase()

    if (ext !in allowedExtensions) {
        throw IllegalArgumentException("サポートされていないファイル拡張子です: $ext")
    }

    // --- YAML 読み込み ---
    val yaml = Yaml()
    return file.inputStream().use { input ->
        yaml.loadAs(input, PdfLayout::class.java)
            ?: throw IllegalArgumentException("YAML の読み込みに失敗しました")
    }

}

// ==============================
// ▼ 座標変換（fontSize はそのままコピー）
// ==============================
fun convertLayout(raw: PdfLayout, converter: PdfPositionConverter): ConvertedLayout {

    val converted = raw.fields.mapValues { (_, pos: FieldPosition) ->
        val (pdfX, pdfY) = converter.toPdfPoint(pos.x.toInt(), pos.y.toInt())
        ConvertedFieldPosition(
            x = pdfX,
            y = pdfY,
            fontSize = pos.fontSize.toInt()
        )
    }

    return ConvertedLayout(converted)
}

// ==============================
// ▼ YAML 出力（fontSize を含める）
// ==============================
fun saveConvertedYaml(layout: ConvertedLayout, outputFile: File) {
    val yaml = Yaml()

    val data = mapOf(
        "fields" to layout.fields.mapValues { (_, pos) ->
            mapOf(
                "x" to pos.x,
                "y" to pos.y,
                "fontSize" to pos.fontSize
            )
        }
    )

    outputFile.writer().use { writer ->
        yaml.dump(data, writer)
    }
}

// ==============================
// ▼ main()：一括処理
// ==============================
fun main() {

    val converter = PdfPositionConverter(
        pdfWidth = 595.32f,
        pdfHeight = 842.04f,
        imageWidth = 1241,
        imageHeight = 1755
    )

    val raw = loadRawLayout("raw_positions.yaml")

    val converted = convertLayout(raw, converter)

    val output = File("src/main/resources/positions/converted_positions.yaml")

    saveConvertedYaml(converted, output)

    println("変換完了 → ${output.absolutePath}")
}
