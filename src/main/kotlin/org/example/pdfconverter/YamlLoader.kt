package org.example.pdfconverter

import org.yaml.snakeyaml.Yaml

data class FieldPosition(
    val x: Float,
    val y: Float,
    val fontSize: Float
)

data class PdfLayout(
    val fields: Map<String, FieldPosition>
)

object LayoutLoader {

    fun loadLayout(): PdfLayout {
        val yaml = Yaml()

        // 読み込む YAML（converted_positions.yaml）
        val input = PdfViewer::class.java.getResourceAsStream("/positions/converted_positions.yaml")
            ?: throw IllegalStateException("converted_positions.yaml が見つかりません")

        val map = yaml.load<Map<String, Any>>(input)

        val fields = (map["fields"] as Map<String, Map<String, Any>>).mapValues { (_, v) ->

            val x = v["x"] ?: error("YAML に x がありません")
            val y = v["y"] ?: error("YAML に y がありません")
            val fontSize = v["fontSize"] ?: error("YAML に fontSize がありません")

            FieldPosition(
                x = (x as Number).toFloat(),
                y = (y as Number).toFloat(),
                fontSize = (fontSize as Number).toFloat()
            )
        }

        return PdfLayout(fields)
    }
}