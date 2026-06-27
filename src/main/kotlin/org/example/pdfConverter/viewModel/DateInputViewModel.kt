package org.example.pdfConverter.viewModel

import javafx.geometry.Insets
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import java.time.LocalDate
import java.time.YearMonth

class DateInputViewModel {

    val yearBox = ComboBox<Int>()
    val monthBox = ComboBox<Int>()
    val dayBox = ComboBox<Int>()

    // ▼ 外部通知用イベント
    private var onChange: (() -> Unit)? = null

    init {
        setupYearBox()
        setupMonthBox()
        setupDayBox()
        setupListeners()
    }

    private fun setupYearBox() {
        val currentYear = LocalDate.now().year
        yearBox.items.setAll(listOf(currentYear, currentYear + 1))

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

    // ============================
    // ▼ 日数の再計算
    // ============================
    private fun refreshDayItems() {
        val y = yearBox.value
        val m = monthBox.value

        val maxDay = if (y != null && m != null) {
            YearMonth.of(y, m).lengthOfMonth()
        } else {
            31
        }

        val current = dayBox.value
        dayBox.items.setAll((1..maxDay).toList())

        // 不正な日付はクリア
        if (current != null && current > maxDay) {
            dayBox.value = null
        }
    }

    // ============================
    // ▼ 変更イベント設定
    // ============================
    private fun setupListeners() {
        yearBox.setOnAction {
            refreshDayItems()
            onChange?.invoke()
        }
        monthBox.setOnAction {
            refreshDayItems()
            onChange?.invoke()
        }
        dayBox.setOnAction {
            onChange?.invoke()
        }
    }

    // ============================
    // ▼ 外部からイベント設定
    // ============================
    fun setOnChange(action: () -> Unit) {
        onChange = action
    }

    // ============================
    // ▼ 値取得（PDF用）
    // ============================
    fun getDate(): Triple<Int?, Int?, Int?> {
        return Triple(yearBox.value, monthBox.value, dayBox.value)
    }

    // ============================
    // ▼ UIとして配置
    // ============================
    fun toHBox(): HBox {
        return HBox(5.0, yearBox, monthBox, dayBox).apply {
            padding = Insets(5.0)
        }
    }
}