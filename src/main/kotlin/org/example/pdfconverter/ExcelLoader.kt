package org.example.pdfconverter

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
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
    val endDay: Int?,

    val institutionYear: Int?,
    val institutionMonth: Int?,
    val institutionDay: Int?,

    val specificDisease: String?

)

// -------------------------
// 共通データ
// -------------------------
data class CommonData(
    val surveyAddress: String,
    val surveyPhone: String,
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
    // 空白セルでもズレない安全な読み取り
    // -------------------------
    private fun safeCell(row: Row, index: Int): String {
        val cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
        return formatter.formatCellValue(cell).trim()
    }

    // -------------------------
    // 日付変換
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
                    if (d.year > currentYear + 10) d.minusYears(100) else d
                } catch (e2: Exception) {
                    System.err.println("Invalid date format in Excel: '$raw'")
                    throw e2
                }
            }
            Triple(date.year, date.monthValue, date.dayOfMonth)
        } catch (e: Exception) {
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

            val insuranceId = safeCell(row, 0)
            val name = safeCell(row, 1)

            if (name.isBlank()) continue

            val furigana = safeCell(row, 2)

            val (birthY, birthM, birthD) = parseDate(safeCell(row, 3))

            val gender = safeCell(row, 4)
            val address = safeCell(row, 5)
            val phone = safeCell(row, 6)
            val careLevel = safeCell(row, 7)

            val (startY, startM, startD) = parseDate(safeCell(row, 8))
            val (endY, endM, endD) = parseDate(safeCell(row, 9))
            val (instY, instM, instD) = parseDate(safeCell(row, 10))

            val specificDisease = safeCell(row, 11).ifBlank { null }

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
                    endDay = endD,

                    institutionYear = instY,
                    institutionMonth = instM,
                    institutionDay = instD,

                    specificDisease = specificDisease
                )
            )
        }

        return list
    }

    // -------------------------
    // 共通データ
    // -------------------------
    private fun loadCommon(workbook: Workbook): CommonData {
        val sheet = workbook.getSheet("共通")
            ?: throw IllegalArgumentException("「共通」シートが見つかりません")

        val row = sheet.getRow(1)
            ?: throw IllegalArgumentException("「共通」シートにデータがありません")

        return CommonData(
            surveyAddress = safeCell(row, 0),
            surveyPhone = safeCell(row, 1),

            facilityName = safeCell(row, 2),
            facilityPhone = safeCell(row, 3),

            institutionName = safeCell(row, 4),
            institutionAddress = safeCell(row, 5),

            agentName = safeCell(row, 6),
            agentPostal = safeCell(row, 7),
            agentAddress = safeCell(row, 8),
            agentPhone = safeCell(row, 9),

            doctorName = safeCell(row, 10),

            clinicName = safeCell(row, 11),
            clinicPostal = safeCell(row, 12),
            clinicAddress = safeCell(row, 13),
            clinicPhone = safeCell(row, 14)
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
