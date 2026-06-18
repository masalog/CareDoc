package org.example.pdfconverter

import javafx.geometry.Insets
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import java.time.LocalDate

class DateInputView {

    val yearBox = ComboBox<Int>()
    val monthBox = ComboBox<Int>()
    val dayBox = ComboBox<Int>()

    init {
        setupYearBox()
        setupMonthBox()
        setupDayBox()
    }

    private fun setupYearBox() {
        val currentYear = LocalDate.now().year
        yearBox.items.addAll((1900..currentYear).reversed())

        yearBox.promptText = "年"
        yearBox.prefWidth = 80.0
    }

    private fun setupMonthBox() {
        monthBox.items.addAll(1..12)

        monthBox.promptText = "月"
        monthBox.prefWidth = 60.0
    }

    private fun setupDayBox() {
        dayBox.items.addAll(1..31)

        dayBox.promptText = "日"
        dayBox.prefWidth = 60.0
    }

    fun getDate(): LocalDate? {
        val y = yearBox.value
        val m = monthBox.value
        val d = dayBox.value

        if (y == null || m == null || d == null) return null

        return try {
            LocalDate.of(y, m, d)
        } catch (e: Exception) {
            null
        }
    }

    fun toHBox(): HBox {
        return HBox(5.0, yearBox, monthBox, dayBox).apply {
            padding = Insets(5.0)
        }
    }
}