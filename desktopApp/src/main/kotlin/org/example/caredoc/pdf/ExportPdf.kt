package org.example.caredoc.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.example.caredoc.model.FieldSelection

fun exportPdf(selections: List<FieldSelection>) {

    // ① テンプレート PDF を読み込む
    val templateStream = {}.javaClass.getResourceAsStream("/templates/template.pdf")
        ?: throw IllegalArgumentException("テンプレート PDF が見つかりません")

    val document = PDDocument.load(templateStream)
    val page = document.getPage(0)

    // ② 日本語フォントを読み込む
    val fontStream = {}.javaClass.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf")
        ?: throw IllegalArgumentException("フォントが見つかりません")

    val font = PDType0Font.load(document, fontStream)

    // ③ append モードで ContentStream を開く
    val content = PDPageContentStream(
        document,
        page,
        AppendMode.APPEND,
        true
    )

    content.setFont(font, 14f)

    // ④ ★ 複数の座標に複数の値を書き込む
    selections.forEach { sel ->
        content.beginText()
        content.newLineAtOffset(sel.x, sel.y)
        content.showText(sel.value)
        content.endText()
    }

    content.close()

    // ⑤ 保存
    document.save("output.pdf")
    document.close()
}