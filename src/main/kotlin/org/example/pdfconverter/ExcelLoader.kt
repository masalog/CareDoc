package org.example.pdfconverter

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// -------------------------
// 個別データ
// -------------------------
data class Member(
    val insuranceIdNumber: String,
    val name: String,
    val furigana: String,

    val birthYear: Int?,
    val birthMonth: Int?,
    val birthDay: Int?,

    val gender: String,
    val address: String,
    val phone: String,
    val careLevel: String,

    val startYear: Int?,
    val startMonth: Int?,
    val startDay: Int?,

    val endYear: Int?,
    val endMonth: Int?,
    val endDay: Int?
)

// -------------------------
// 共通データ
// -------------------------
data class CommonData(
    val facilityName: String,
    val facilityPhone: String,
    val institutionName: String,
    val institutionAddress: String,
    val agentName: String,
    val agentPostal: String,
    val agentAddress: String,
    val agentPhone: String,
    val doctorName: String,
    val clinicName: String,
    val clinicPostal: String,
    val clinicAddress: String,
    val clinicPhone: String
)

object ExcelLoader {

    private val formatter = DataFormatter()

    private val jpFormatter = DateTimeFormatter.ofPattern("yyyy/M/d")
    private val usFormatter = DateTimeFormatter.ofPattern("M/d/yy")

    // -------------------------
    // ✅ 日付変換（ログ付き安定版）
    // -------------------------
    private fun parseDate(text: String): Triple<Int?, Int?, Int?> {
        val raw = text.trim()
        if (raw.isBlank()) return Triple(null, null, null)

        return try {
            val date = try {
                LocalDate.parse(raw, jpFormatter)
            } catch (e1: Exception) {
                try {
                    val d = LocalDate.parse(raw, usFormatter)
                    val currentYear = LocalDate.now().year

                    if (d.year > currentYear + 10) {
                        d.minusYears(100)
                    } else {
                        d
                    }
                } catch (e2: Exception) {
                    System.err.println("Invalid date format in Excel (JP/US failed): '$raw'")
                    throw e2
                }
            }

            Triple(date.year, date.monthValue, date.dayOfMonth)

        } catch (e: Exception) {
            System.err.println("Date parse error: '$raw' (${e.message})")
            Triple(null, null, null)
        }
    }

    // -------------------------
    // 個別データ
    // -------------------------
    private fun loadMembers(workbook: Workbook): List<Member> {
        val sheet = workbook.getSheet("個別")
            ?: throw IllegalArgumentException("「個別」シートが見つかりません")

        val list = mutableListOf<Member>()

        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue

            val insuranceId = formatter.formatCellValue(row.getCell(0))
            val name = formatter.formatCellValue(row.getCell(1))

            // 空行スキップ
            if (name.isBlank()) continue

            val furigana = formatter.formatCellValue(row.getCell(2))

            val (birthY, birthM, birthD) =
                parseDate(formatter.formatCellValue(row.getCell(3)))

            val gender = formatter.formatCellValue(row.getCell(4))
            val address = formatter.formatCellValue(row.getCell(5))
            val phone = formatter.formatCellValue(row.getCell(6))
            val careLevel = formatter.formatCellValue(row.getCell(7))

            val (startY, startM, startD) =
                parseDate(formatter.formatCellValue(row.getCell(8)))

            val (endY, endM, endD) =
                parseDate(formatter.formatCellValue(row.getCell(9)))

            list.add(
                Member(
                    insuranceIdNumber = insuranceId,
                    name = name,
                    furigana = furigana,

                    birthYear = birthY,
                    birthMonth = birthM,
                    birthDay = birthD,

                    gender = gender,
                    address = address,
                    phone = phone,
                    careLevel = careLevel,

                    startYear = startY,
                    startMonth = startM,
                    startDay = startD,

                    endYear = endY,
                    endMonth = endM,
                    endDay = endD
                )
            )
        }

        return list
    }

    // -------------------------
    // 共通データ（1件想定）
    // -------------------------
    private fun loadCommon(workbook: Workbook): CommonData {
        val sheet = workbook.getSheet("共通")
            ?: throw IllegalArgumentException("「共通」シートが見つかりません")

        val row = sheet.getRow(1)
            ?: throw IllegalArgumentException("「共通」シートにデータがありません")

        return CommonData(
            facilityName = formatter.formatCellValue(row.getCell(0)),
            facilityPhone = formatter.formatCellValue(row.getCell(1)),
            institutionName = formatter.formatCellValue(row.getCell(2)),
            institutionAddress = formatter.formatCellValue(row.getCell(3)),
            agentName = formatter.formatCellValue(row.getCell(4)),
            agentPostal = formatter.formatCellValue(row.getCell(5)),
            agentAddress = formatter.formatCellValue(row.getCell(6)),
            agentPhone = formatter.formatCellValue(row.getCell(7)),
            doctorName = formatter.formatCellValue(row.getCell(8)),
            clinicName = formatter.formatCellValue(row.getCell(9)),
            clinicPostal = formatter.formatCellValue(row.getCell(10)),
            clinicAddress = formatter.formatCellValue(row.getCell(11)),
            clinicPhone = formatter.formatCellValue(row.getCell(12))
        )
    }

    // -------------------------
    // 統合
    // -------------------------
    fun loadAll(filePath: String = "members.xlsx"):
            Pair<List<Member>, CommonData> {

        val file = File(filePath)

        WorkbookFactory.create(file).use { workbook ->
            val members = loadMembers(workbook)
            val common = loadCommon(workbook)
            return members to common
        }
    }
}