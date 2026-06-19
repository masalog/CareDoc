package org.example.pdfconverter

import javafx.geometry.Insets
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import java.time.LocalDate

class DateInputView {

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

    // ============================
    // ▼ 変更イベント設定
    // ============================
    private fun setupListeners() {
        yearBox.setOnAction { onChange?.invoke() }
        monthBox.setOnAction { onChange?.invoke() }
        dayBox.setOnAction { onChange?.invoke() }
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