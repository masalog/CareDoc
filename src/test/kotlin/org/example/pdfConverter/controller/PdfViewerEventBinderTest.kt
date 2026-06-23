package org.example.pdfConverter.controller

import io.kotest.core.spec.style.BehaviorSpec
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.view.PdfViewerUI
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import org.example.pdfConverter.testUtil.TestData
import org.mockito.kotlin.*

class PdfViewerEventBinderTest : BehaviorSpec({

    val viewModel = mock<PdfUpdateViewModel>()
    val ui = mock<PdfViewerUI>()
    val errorHandler = mock<ErrorHandler>()

    val members = TestData.members
    val common = TestData.common

    fun binder(commonData: org.example.pdfConverter.model.CommonData?) =
        PdfViewerEventBinder(
            view = ui,
            viewModel = viewModel,
            members = members,
            common = commonData,
            stage = mock(),
            errorHandler = errorHandler
        )

    Given("updatePdf のロジック") {

        // =========================================================
        // 正常系（名前一致パターン）
        // =========================================================
        When("選択された名前に対応する Member が存在する場合") {

            whenever(ui.getSelectedName()).thenReturn("山田太郎")
            whenever(ui.getReason()).thenReturn("理由")
            whenever(ui.getDate()).thenReturn(Triple(2024, 1, 1))

            val handlerCaptor = argumentCaptor<() -> Unit>()

            doNothing().whenever(ui).setOnNameChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnReasonChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnDateChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnExportClicked(any<() -> Unit>())

            val target = binder(common)
            target.bind()

            verify(ui).setOnNameChanged(handlerCaptor.capture())

            handlerCaptor.firstValue.invoke()

            Then("一致する Member を使って updatePdf が呼ばれる") {
                verify(viewModel).updatePdf(
                    member = members[0],
                    common = common,
                    reason = "理由",
                    date = Triple(2024, 1, 1)
                )
            }
        }

        // =========================================================
        // ✅ 境界値：Member が見つからない
        // =========================================================
        When("Member が見つからない") {

            whenever(ui.getSelectedName()).thenReturn("不明")
            whenever(ui.getReason()).thenReturn("理由")
            whenever(ui.getDate()).thenReturn(Triple(2024, 1, 1))

            val handlerCaptor = argumentCaptor<() -> Unit>()

            doNothing().whenever(ui).setOnNameChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnReasonChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnDateChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnExportClicked(any<() -> Unit>())

            val target = binder(common)
            target.bind()

            // ✅ ハンドラ取得
            verify(ui).setOnNameChanged(handlerCaptor.capture())

            // ✅ イベント発火
            handlerCaptor.firstValue.invoke()

            Then("member=null で呼ばれる") {
                verify(viewModel).updatePdf(
                    member = null,
                    common = common,
                    reason = "理由",
                    date = Triple(2024, 1, 1)
                )
            }
        }

        // =========================================================
        // ✅ 境界値：common が null
        // =========================================================
        When("common が null") {

            whenever(ui.getSelectedName()).thenReturn("山田太郎")

            val handlerCaptor = argumentCaptor<() -> Unit>()

            doNothing().whenever(ui).setOnNameChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnReasonChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnDateChanged(any<() -> Unit>())
            doNothing().whenever(ui).setOnExportClicked(any<() -> Unit>())

            val target = binder(null)
            target.bind()

            // ★ ハンドラ取得
            verify(ui).setOnNameChanged(handlerCaptor.capture())

            // ★ イベント発火
            handlerCaptor.firstValue.invoke()

            Then("updatePdf は呼ばれない") {
                verify(viewModel, never()).updatePdf(
                    anyOrNull(),
                    any(),
                    any(),
                    any()
                )
            }
        }

        // =========================================================
        // ✅ 異常系：UIイベント未発火
        // =========================================================
        When("イベントが発火しない") {

            whenever(ui.getSelectedName()).thenReturn("山田太郎")

            doNothing().whenever(ui).setOnNameChanged(any())
            doNothing().whenever(ui).setOnReasonChanged(any())
            doNothing().whenever(ui).setOnDateChanged(any())
            doNothing().whenever(ui).setOnExportClicked(any())

            val target = binder(common)
            target.bind()

            Then("updatePdf は一度も呼ばれない") {
                verify(viewModel, never()).updatePdf(
                    anyOrNull(),
                    any(),
                    any(),
                    any()
                )
            }
        }
    }
})