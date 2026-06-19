package org.example.pdfconverter

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType0Font
import java.io.File
import java.io.InputStream

class PdfEditor {

    fun editPdf(
        member: Member,
        common: CommonData,
        applyYear: Int?,
        applyMonth: Int?,
        applyDay: Int?
    ): File {

        val output = File("edited.pdf")
        val layout = LayoutLoader.loadLayout()
        val template = extractTemplate()

        PDDocument.load(template).use { doc ->
            val page = doc.getPage(0)
            val font = PDType0Font.load(doc, getFont())

            PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true).use { c ->

                fun drawText(key: String, value: String) {
                    layout.fields[key]?.let {
                        c.beginText()
                        c.setFont(font, it.fontSize)
                        c.newLineAtOffset(it.x, it.y)
                        c.showText(value)
                        c.endText()
                    }
                }

                fun drawCircle(key: String) {
                    layout.fields[key]?.let {
                        c.beginText()
                        c.setFont(font, it.fontSize)
                        c.newLineAtOffset(it.x, it.y)
                        c.showText("〇")
                        c.endText()
                    }
                }

                // 個別データ
                drawText("Insurance ID Number", member.insuranceIdNumber)
                drawText("name", member.name)
                drawText("furigana", member.furigana)

                member.birthYear?.let { drawText("birthYear", it.toString()) }
                member.birthMonth?.let { drawText("birthMonth", it.toString()) }
                member.birthDay?.let { drawText("birthDay", it.toString()) }

                drawText("address", member.address)
                drawText("phone", member.phone)

                when (member.gender) {
                    "男" -> drawCircle("genderMale")
                    "女" -> drawCircle("genderFemale")
                }

                // 要介護区分
                val careKey = when (member.careLevel) {
                    "要介護1" -> "Long-term Care Level 1"
                    "要介護2" -> "Long-term Care Level 2"
                    "要介護3" -> "Long-term Care Level 3"
                    "要介護4" -> "Long-term Care Level 4"
                    "要介護5" -> "Long-term Care Level 5"
                    "要支援1" -> "Support Level 1"
                    "要支援2" -> "Support Level 2"
                    else -> null
                }
                careKey?.let { drawCircle(it) }

                // 有効期間（Excel の値）
                member.startYear?.let { drawText("startYear", it.toString()) }
                member.startMonth?.let { drawText("startMonth", it.toString()) }
                member.startDay?.let { drawText("startDay", it.toString()) }

                member.endYear?.let { drawText("endYear", it.toString()) }
                member.endMonth?.let { drawText("endMonth", it.toString()) }
                member.endDay?.let { drawText("endDay", it.toString()) }

                drawCircle("isFacility")
                drawCircle("agentCategory")
            }

            doc.save(output)
        }

        return output
    }

    private fun extractTemplate(): File {
        val temp = File.createTempFile("template", ".pdf")
        temp.outputStream().use {
            getTemplate().copyTo(it)
        }
        return temp
    }

    private fun getTemplate(): InputStream =
        javaClass.getResourceAsStream("/templates/template.pdf")!!

    private fun getFont(): InputStream =
        javaClass.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf")!!
}
