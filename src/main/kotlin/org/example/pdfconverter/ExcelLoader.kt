package org.example.pdfconverter

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

data class Member(
    val name: String,
    val furigana: String,
    val birthYear: Int,
    val birthMonth: Int,
    val birthDay: Int,
    val gender: String,
    val address: String,
    val phone: String
)

object ExcelLoader {

    private val formatter = DataFormatter()

    fun loadMembers(filePath: String = "members.xlsx"): List<Member> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Excel ファイルが見つかりません: $filePath")
        }

        val members = mutableListOf<Member>()

        WorkbookFactory.create(file).use { workbook ->
            if (workbook.numberOfSheets == 0) {
                throw IllegalArgumentException("Excel ファイルにシートが含まれていません: $filePath")
            }

            val sheet = workbook.getSheetAt(0)

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                val name = formatter.formatCellValue(row.getCell(0))
                val furigana = formatter.formatCellValue(row.getCell(1))
                val birthYear = formatter.formatCellValue(row.getCell(2)).toIntOrNull() ?: 0
                val birthMonth = formatter.formatCellValue(row.getCell(3)).toIntOrNull() ?: 0
                val birthDay = formatter.formatCellValue(row.getCell(4)).toIntOrNull() ?: 0
                val gender = formatter.formatCellValue(row.getCell(5))
                val address = formatter.formatCellValue(row.getCell(6))
                val phone = formatter.formatCellValue(row.getCell(7))

                members.add(
                    Member(
                        name,
                        furigana,
                        birthYear,
                        birthMonth,
                        birthDay,
                        gender,
                        address,
                        phone
                    )
                )
            }
        }

        return members
    }
}
