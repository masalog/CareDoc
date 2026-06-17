package org.example.pdfconverter

import org.apache.poi.ss.usermodel.DataFormatter
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

    // -------------------------
    // 個別シート読み込み
    // -------------------------
    fun loadMembers(filePath: String = "members.xlsx"): List<Member> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Excel ファイルが見つかりません: $filePath")
        }

        val members = mutableListOf<Member>()

        WorkbookFactory.create(file).use { workbook ->
            val sheet = workbook.getSheet("個別")
                ?: throw IllegalArgumentException("「個別」シートが見つかりません: $filePath")

            // 1行目はヘッダーなので 2行目から
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
        }

        return members
    }

    // -------------------------
    // 共通シート読み込み（複数行対応・.add 使用）
    // -------------------------
    fun loadCommon(filePath: String = "members.xlsx"): List<CommonData> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Excel ファイルが見つかりません: $filePath")
        }

        val list = mutableListOf<CommonData>()

        WorkbookFactory.create(file).use { workbook ->
            val sheet = workbook.getSheet("共通")
                ?: throw IllegalArgumentException("「共通」シートが見つかりません: $filePath")

            // 1行目はヘッダーなので 2行目から
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
        }

        return list
    }
}
