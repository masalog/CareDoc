package org.example.pdfConverter.controller

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import org.mockito.kotlin.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.example.pdfConverter.factory.PdfViewerFactory
import org.example.pdfConverter.model.*
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.testutil.TestData
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel

class PdfViewerControllerTest : BehaviorSpec({

    // ------------------------------------------------------------
    // モック
    // ------------------------------------------------------------
    val errorHandler = mock<ErrorHandler>()
    val initializer = mock<PdfViewerInitializer>()
    val factory = mock<PdfViewerFactory>()
    val stage = mock<Stage>()

    val binderMock: (
        PdfViewerView,
        PdfUpdateViewModel,
        List<Member>,
        CommonData?,
        Stage,
        ErrorHandler
    ) -> Unit = { _, _, _, _, _, _ -> }

    // ------------------------------------------------------------
    // ★ TestData 使用
    // ------------------------------------------------------------
    val dummyMembers = TestData.members
    val dummyCommon = TestData.common

    val dummyInitialData = InitialData(
        members = dummyMembers,
        common = dummyCommon,
        templatePdf = TestData.initialData.templatePdf
    )

    val dummyViewModel = mock<PdfUpdateViewModel>()

    val dummyView = mock<PdfViewerView> {
        on { root } doReturn BorderPane()
    }

    // ============================================================
    // ✅ 正常系
    // ============================================================
    Given("初期データが正常に読み込める状況") {

        When("ビュー生成が正常に行われる場合") {

            Then("BorderPane が返される") {

                val controller = PdfViewerController(
                    errorHandler,
                    initializer,
                    factory,
                    binderMock
                )

                whenever(initializer.loadInitialData()).thenReturn(dummyInitialData)
                whenever(factory.create(dummyInitialData))
                    .thenReturn(dummyViewModel to dummyView)

                val result = controller.createView(stage)

                result.shouldBeInstanceOf<BorderPane>()
            }
        }
    }

    // ============================================================
    // ✅ 境界値
    // ============================================================
    Given("members が空の境界値") {

        When("空でも初期化") {

            Then("ビュー生成は成功") {

                val controller = PdfViewerController(
                    errorHandler,
                    initializer,
                    factory,
                    binderMock
                )

                val emptyData = dummyInitialData.copy(members = emptyList())

                whenever(initializer.loadInitialData()).thenReturn(emptyData)
                whenever(factory.create(emptyData))
                    .thenReturn(dummyViewModel to dummyView)

                val result = controller.createView(stage)

                result.shouldBeInstanceOf<BorderPane>()

                verify(factory).create(emptyData)
            }
        }
    }

    // ============================================================
    // ✅ 異常系
    // ============================================================
    Given("初期データ読み込み時に例外") {

        When("initializer が例外を投げる") {

            Then("ErrorHandler が呼ばれる") {

                val controller = PdfViewerController(
                    errorHandler,
                    initializer,
                    factory,
                    binderMock
                )

                whenever(initializer.loadInitialData())
                    .thenThrow(RuntimeException("読み込み失敗"))

                // ★ 例外が外に漏れないように必ず囲む
                shouldNotThrowAny {
                    controller.createView(stage)
                }

                verify(errorHandler).showError(
                    eq("初期データ読込エラー"),
                    any()
                )
            }
        }
    }

    // ============================================================
    // ✅ dispose
    // ============================================================
    Given("ビュー生成後") {

        When("dispose() 呼び出し") {

            Then("ViewModel.dispose が呼ばれる") {

                val controller = PdfViewerController(
                    errorHandler,
                    initializer,
                    factory,
                    binderMock
                )

                whenever(initializer.loadInitialData()).thenReturn(dummyInitialData)
                whenever(factory.create(dummyInitialData))
                    .thenReturn(dummyViewModel to dummyView)

                controller.createView(stage)
                controller.dispose()

                verify(dummyViewModel).dispose()
            }
        }
    }
})