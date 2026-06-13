package org.example.pdfconverter

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

    fun loadMembers(filePath: String = "members.xlsx"): List<Member> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Excel ファイルが見つかりません: $filePath")
        }

        val members = mutableListOf<Member>()

        WorkbookFactory.create(file).use { workbook ->
            val sheet = workbook.getSheetAt(0)

            // 1行目はヘッダーなので 1 から開始
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                val name = row.getCell(0)?.stringCellValue ?: ""
                val furigana = row.getCell(1)?.stringCellValue ?: ""
                val birthYear = row.getCell(2)?.numericCellValue?.toInt() ?: 0
                val birthMonth = row.getCell(3)?.numericCellValue?.toInt() ?: 0
                val birthDay = row.getCell(4)?.numericCellValue?.toInt() ?: 0
                val gender = row.getCell(5)?.stringCellValue ?: ""
                val address = row.getCell(6)?.stringCellValue ?: ""
                val phone = row.getCell(7)?.stringCellValue ?: ""

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
