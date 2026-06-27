package org.example.pdfConverter.util

import org.yaml.snakeyaml.Yaml
import java.io.File

// ==============================
// ▼ データクラス
// ==============================
data class RawFieldPosition(
    val x: Int,
    val y: Int,
    val fontSize: Int
)

data class RawLayout(
    val fields: Map<String, RawFieldPosition>
)

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
fun loadRawLayout(path: String): RawLayout {
    // --- 拡張子ホワイトリストチェック ---
    val allowedExtensions = setOf("yaml", "yml")
    val ext = File(path).extension.lowercase()

    if (ext !in allowedExtensions) {
        throw IllegalArgumentException("サポートされていないファイル拡張子です: $ext")
    }

    // --- YAML 読み込み ---
    val yaml = Yaml()
    val input = File(path).inputStream()

    val map = yaml.load<Map<String, Any>>(input)

    val fields = (map["fields"] as Map<String, Map<String, Any>>).mapValues { (_, v) ->
        RawFieldPosition(
            x = (v["x"] as Number).toInt(),
            y = (v["y"] as Number).toInt(),
            fontSize = (v["fontSize"] as Number).toInt()
        )
    }

    return RawLayout(fields)
}

// ==============================
// ▼ 座標変換（fontSize はそのままコピー）
// ==============================
fun convertLayout(raw: RawLayout, converter: PdfPositionConverter): ConvertedLayout {

    val converted = raw.fields.mapValues { (_, pos) ->
        val (pdfX, pdfY) = converter.toPdfPoint(pos.x, pos.y)
        ConvertedFieldPosition(
            x = pdfX,
            y = pdfY,
            fontSize = pos.fontSize
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

    val raw = loadRawLayout("src/main/resources/positions/raw_positions.yaml")

    val converted = convertLayout(raw, converter)

    val output = File("src/main/resources/positions/converted_positions.yaml")

    saveConvertedYaml(converted, output)

    println("変換完了 → ${output.absolutePath}")
}
