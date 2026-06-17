package org.example.pdfconverter

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

// -------------------------
// 個別データ
// -------------------------
data class Member(
    val name: String,
    val furigana: String,
    val birthYear: Int,
    val birthMonth: Int,
    val birthDay: Int,
    val gender: String,
    val address: String,
    val phone: String,
    val insuranceIdNumber: String,
    val careLevel: String
)

// -------------------------
// 共通データ（将来複数行対応）
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

// -------------------------
// Excel ローダー本体
// -------------------------
object ExcelLoader {

    private val formatter = DataFormatter()

    // ============================================================
    // 内部メソッド（Workbook を受け取る）
    // ============================================================

    private fun loadMembers(workbook: Workbook): List<Member> {
        val sheet = workbook.getSheet("個別")
            ?: throw IllegalArgumentException("「個別」シートが見つかりません")

        val members = mutableListOf<Member>()

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue

            members.add(
                Member(
                    name = formatter.formatCellValue(row.getCell(0)),
                    furigana = formatter.formatCellValue(row.getCell(1)),
                    birthYear = formatter.formatCellValue(row.getCell(2)).toIntOrNull() ?: 0,
                    birthMonth = formatter.formatCellValue(row.getCell(3)).toIntOrNull() ?: 0,
                    birthDay = formatter.formatCellValue(row.getCell(4)).toIntOrNull() ?: 0,
                    gender = formatter.formatCellValue(row.getCell(5)),
                    address = formatter.formatCellValue(row.getCell(6)),
                    phone = formatter.formatCellValue(row.getCell(7)),
                    insuranceIdNumber = formatter.formatCellValue(row.getCell(8)),
                    careLevel = formatter.formatCellValue(row.getCell(9))
                )
            )
        }

        return members
    }

    private fun loadCommon(workbook: Workbook): List<CommonData> {
        val sheet = workbook.getSheet("共通")
            ?: throw IllegalArgumentException("「共通」シートが見つかりません")

        val list = mutableListOf<CommonData>()

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue

            list.add(
                CommonData(
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
            )
        }

        return list
    }

    // ============================================================
    // 新しい統合 API（Workbook を 1 回だけ開く）
    // ============================================================

    fun loadAll(filePath: String = "members.xlsx"): Pair<List<Member>, List<CommonData>> {
        val file = File(filePath)
        WorkbookFactory.create(file).use { workbook ->
            val members = loadMembers(workbook)
            val common = loadCommon(workbook)
            return members to common
        }
    }
}
