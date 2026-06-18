package org.example.pdfconverter

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.time.LocalDate

/**
 * 有効期間（開始日・終了日）をまとめて扱う UI コンポーネント
 */
class ValidPeriodInputView {

    // 開始日
    private val startLabel = Label("有効期間（開始）")
    val startDate = DateInputView()

    // 終了日
    private val endLabel = Label("有効期間（終了）")
    val endDate = DateInputView()

    // ▼ 日付取得
    fun getStartDate(): LocalDate? = startDate.getDate()
    fun getEndDate(): LocalDate? = endDate.getDate()

    // ▼ 変更イベント（★これが今回のキモ）
    fun setOnChange(handler: () -> Unit) {

        // 開始日
        startDate.yearBox.setOnAction { handler() }
        startDate.monthBox.setOnAction { handler() }
        startDate.dayBox.setOnAction { handler() }

        // 終了日
        endDate.yearBox.setOnAction { handler() }
        endDate.monthBox.setOnAction { handler() }
        endDate.dayBox.setOnAction { handler() }
    }

    // ▼ UI生成
    fun toVBox(): VBox {
        return VBox(
            8.0,
            HBox(10.0, startLabel, startDate.toHBox()),
            HBox(10.0, endLabel, endDate.toHBox())
        ).apply {
            padding = Insets(10.0)
        }
    }
}


