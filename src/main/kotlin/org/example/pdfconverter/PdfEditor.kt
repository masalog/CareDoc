package org.example.pdfconverter

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType0Font
import java.io.File
import java.io.InputStream

class PdfEditor {

    fun editPdf(
        member: Member?,
        common: CommonData,
        applyYear: Int?,
        applyMonth: Int?,
        applyDay: Int?,
        changeRequestReason: String?
    ): File {

        // ▼ 固定ファイルではなく一時ファイルを使用
        val output = File.createTempFile("edited-", ".pdf").apply { deleteOnExit() }
        val layout = LayoutLoader.loadLayout()

        // ▼ テンプレートは InputStream から直接ロード
        getTemplate().use { templateStream ->
            PDDocument.load(templateStream).use { doc ->
                val page = doc.getPage(0)
                val font = PDType0Font.load(doc, getFont())

                PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true).use { c ->

                    fun drawText(key: String, value: String) {
                        layout.fields[key]?.let {

                            val lines = value.split("\n")

                            lines.forEachIndexed { i, line ->
                                if (line.isEmpty()) return@forEachIndexed

                                c.beginText()
                                c.setFont(font, it.fontSize)
                                c.newLineAtOffset(it.x, it.y - i * (it.fontSize + 2))
                                c.showText(line)
                                c.endText()
                            }
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
                    if (member != null) {

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

                        // 有効期間
                        member.startYear?.let { drawText("startYear", it.toString()) }
                        member.startMonth?.let { drawText("startMonth", it.toString()) }
                        member.startDay?.let { drawText("startDay", it.toString()) }

                        member.endYear?.let { drawText("endYear", it.toString()) }
                        member.endMonth?.let { drawText("endMonth", it.toString()) }
                        member.endDay?.let { drawText("endDay", it.toString()) }

                        // 入所日
                        member.institutionYear?.let { drawText("institutionYear", it.toString()) }
                        member.institutionMonth?.let { drawText("institutionMonth", it.toString()) }
                        member.institutionDay?.let { drawText("institutionDay", it.toString()) }

                        // 特定疾病
                        drawText("specificDisease", member.specificDisease ?: "")

                        // 共通データ
                        drawText("Survey Location Address", common.surveyAddress)
                        drawText("Survey Location Phone", common.surveyPhone)
                        drawText("facilityName", common.facilityName)
                        drawText("facilityPhone", common.facilityPhone)
                        drawText("institutionName", common.institutionName)
                        drawText("institutionAddress", common.institutionAddress)
                        drawText("agentName", common.agentName)
                        drawText("agentPostal", common.agentPostal)
                        drawText("agentAddress", common.agentAddress)
                        drawText("agentPhone", common.agentPhone)
                        drawText("doctorName", common.doctorName)
                        drawText("clinicName", common.clinicName)
                        drawText("clinicPostal", common.clinicPostal)
                        drawText("clinicAddress", common.clinicAddress)
                        drawText("clinicPhone", common.clinicPhone)

                        drawCircle("isFacility")
                        drawCircle("agentCategory")
                    }


                    applyYear?.let { drawText("applyYear", it.toString()) }
                    applyMonth?.let { drawText("applyMonth", it.toString()) }
                    applyDay?.let { drawText("applyDay", it.toString()) }


                    println("reason raw = [$changeRequestReason]")
                        drawText("Change Request Reason", changeRequestReason ?: "")

                }

                doc.save(output)
            }
        }

        return output
    }

    private fun getTemplate(): InputStream =
        javaClass.getResourceAsStream("/templates/template.pdf")!!

    private fun getFont(): InputStream =
        javaClass.getResourceAsStream("/fonts/NotoSansJP-Regular.ttf")!!
}
