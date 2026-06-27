package org.example.pdfConverter.repository

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.example.pdfConverter.model.Member
import org.example.pdfConverter.model.CommonData

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
            } catch (_: Exception) {
                val d = LocalDate.parse(raw, usFormatter)
                val currentYear = LocalDate.now().year
                if (d.year > currentYear + 10) d.minusYears(100) else d
            }
            Triple(date.year, date.monthValue, date.dayOfMonth)
        } catch (_: Exception) {
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
                    institutionDay = instD

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

        // --- 拡張子チェック（ホワイトリスト） ---
        val allowedExtensions = setOf("xlsx", "xls")
        val ext = file.extension.lowercase()

        if (ext !in allowedExtensions) {
            throw IllegalArgumentException("不正なファイル形式です: .$ext は許可されていません")
        }

        // --- MIME タイプチェック（推奨） ---
        val mime = java.nio.file.Files.probeContentType(file.toPath())
        val allowedMime = setOf(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel"
        )

        if (mime !in allowedMime) {
            throw IllegalArgumentException("不正な MIME タイプです: $mime")
        }

        // --- Excel 読み込み ---
        WorkbookFactory.create(file).use { workbook ->
            val members = loadMembers(workbook)
            val common = loadCommon(workbook)
            return members to common
        }
    }

}
