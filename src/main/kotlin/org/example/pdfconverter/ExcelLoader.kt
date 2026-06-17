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
    val phone: String
    val phone: String,
    val insuranceIdNumber: String,
    val careLevel: String
)

)

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
}
