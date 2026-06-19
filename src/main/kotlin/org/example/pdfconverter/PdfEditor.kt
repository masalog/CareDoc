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
        dateInput: DateInputView,
        validPeriodInput: ValidPeriodInputView
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

                // Excelデータ
                val values = mapOf(
                    "name" to member.name,
                    "furigana" to member.furigana,
                    "birthYear" to member.birthYear.toString(),
                    "birthMonth" to member.birthMonth.toString(),
                    "birthDay" to member.birthDay.toString(),
                    "address" to member.address,
                    "phone" to member.phone,
                    "Insurance ID Number" to member.insuranceIdNumber,
                    "facilityName" to common.facilityName,
                    "facilityPhone" to common.facilityPhone,
                    "institutionName" to common.institutionName,
                    "institutionAddress" to common.institutionAddress,
                    "agentName" to common.agentName,
                    "agentPostal" to common.agentPostal,
                    "agentAddress" to common.agentAddress,
                    "agentPhone" to common.agentPhone,
                    "doctorName" to common.doctorName,
                    "clinicName" to common.clinicName,
                    "clinicPostal" to common.clinicPostal,
                    "clinicAddress" to common.clinicAddress,
                    "clinicPhone" to common.clinicPhone
                )

                for ((key, value) in values) {
                    drawText(key, value)
                }

                // 日付
                drawDateParts("apply", dateInput, ::drawText)
                drawDateParts("start", validPeriodInput.startDate, ::drawText)
                drawDateParts("end", validPeriodInput.endDate, ::drawText)

                // 性別
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

                drawCircle("isFacility")
                drawCircle("agentCategory")
            }

            doc.save(output)
        }

        return output
    }

    fun editPdfDateOnly(
        dateInput: DateInputView,
        validPeriodInput: ValidPeriodInputView
    ): File {

        val output = File("edited.pdf")
        val layout = LayoutLoader.loadLayout()
        val template = extractTemplate()

        PDDocument.load(template).use { doc ->
            val page = doc.getPage(0)
            val font = PDType0Font.load(doc, getFont())

            PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true).use { c ->

                fun drawText(k: String, v: String) {
                    layout.fields[k]?.let {
                        c.beginText()
                        c.setFont(font, it.fontSize)
                        c.newLineAtOffset(it.x, it.y)
                        c.showText(v)
                        c.endText()
                    }
                }

                drawDateParts("apply", dateInput, ::drawText)
                drawDateParts("start", validPeriodInput.startDate, ::drawText)
                drawDateParts("end", validPeriodInput.endDate, ::drawText)
            }

            doc.save(output)
        }

        return output
    }

    private fun drawDateParts(
        prefix: String,
        dateInput: DateInputView,
        drawText: (String, String) -> Unit
    ) {
        val y = dateInput.yearBox.value
        val m = dateInput.monthBox.value
        val d = dateInput.dayBox.value

        if (y != null) drawText("${prefix}Year", y.toString())
        if (m != null) drawText("${prefix}Month", m.toString())
        if (d != null) drawText("${prefix}Day", d.toString())
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
