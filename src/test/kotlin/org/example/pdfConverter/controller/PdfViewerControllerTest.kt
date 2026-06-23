package org.example.pdfConverter.controller

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import org.mockito.kotlin.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.example.pdfConverter.factory.PdfViewerFactory
import org.example.pdfConverter.model.*
import org.example.pdfConverter.service.ErrorHandler
import org.example.pdfConverter.service.PdfViewerInitializer
import org.example.pdfConverter.view.PdfViewerView
import org.example.pdfConverter.viewModel.PdfUpdateViewModel
import java.io.File

class PdfViewerControllerTest : BehaviorSpec({

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
    // ★ ダミーデータ（完全版）
    // ------------------------------------------------------------
    val dummyMembers = listOf(
        Member(
            insuranceIdNumber = "0001",
            name = "山田太郎",
            furigana = "ヤマダタロウ",
            birthYear = 1950,
            birthMonth = 5,
            birthDay = 20,
            gender = "男",
            address = "福岡市中央区天神1-1-1",
            phone = "090-1111-1111",
            careLevel = "要介護1",
            startYear = 2024,
            startMonth = 1,
            startDay = 1,
            endYear = null,
            endMonth = null,
            endDay = null,
            institutionYear = null,
            institutionMonth = null,
            institutionDay = null,
            specificDisease = "なし"
        )
    )

    val dummyCommon = CommonData(
        surveyAddress = "福岡市中央区薬院1-2-3",
        surveyPhone = "092-111-2222",
        facilityName = "テスト介護施設",
        facilityPhone = "092-333-4444",
        institutionName = "テスト医療機関",
        institutionAddress = "福岡市博多区博多駅前1-1-1",
        agentName = "担当者A",
        agentPostal = "8100001",
        agentAddress = "福岡市中央区天神2-2-2",
        agentPhone = "092-555-6666",
        doctorName = "医師B",
        clinicName = "テストクリニック",
        clinicPostal = "8100002",
        clinicAddress = "福岡市中央区大名1-1-1",
        clinicPhone = "092-777-8888"
    )

    val dummyInitialData = InitialData(
        members = dummyMembers,
        common = dummyCommon,
        templatePdf = File("dummy.pdf")
    )

    val dummyViewModel = mock<PdfUpdateViewModel>()
    val dummyView = mock<PdfViewerView> {
        on { root } doReturn BorderPane()
    }

    // ============================================================
    // 正常系
    // ============================================================
    Given("初期データが正常に読み込める状況") {

        When("ビュー生成が正常に行われる場合") {

            Then("BorderPane が返される") {

                val controller = PdfViewerController(errorHandler, initializer, factory, binderMock)

                whenever(initializer.loadInitialData()).thenReturn(dummyInitialData)
                whenever(factory.create(dummyInitialData)).thenReturn(dummyViewModel to dummyView)

                val result = controller.createView(stage)

                result.shouldBeInstanceOf<BorderPane>()

            }
        }
    }


    // ============================================================
    // 境界値
    // ============================================================
    Given("members が空の境界値") {

        When("空でも初期化") {

            Then("ビュー生成は成功") {

                val controller = PdfViewerController(errorHandler, initializer, factory, binderMock)

                val emptyData = dummyInitialData.copy(members = emptyList())

                whenever(initializer.loadInitialData()).thenReturn(emptyData)
                whenever(factory.create(emptyData)).thenReturn(dummyViewModel to dummyView)

                controller.createView(stage).shouldBeInstanceOf<BorderPane>()
                verify(factory).create(emptyData)
            }
        }
    }

    // ============================================================
    // 異常系
    // ============================================================
    Given("初期データ読み込み時に例外") {

        When("initializer が例外を投げる") {

            Then("ErrorHandler が呼ばれる") {

                val controller = PdfViewerController(errorHandler, initializer, factory, binderMock)

                whenever(initializer.loadInitialData()).thenThrow(RuntimeException("読み込み失敗"))

                controller.createView(stage)
                verify(errorHandler).showError(eq("初期データ読込エラー"), any())
            }
        }
    }

    // ============================================================
    // dispose
    // ============================================================
    Given("ビュー生成後") {

        When("dispose() 呼び出し") {

            Then("ViewModel.dispose が呼ばれる") {

                val controller = PdfViewerController(errorHandler, initializer, factory, binderMock)

                whenever(initializer.loadInitialData()).thenReturn(dummyInitialData)
                whenever(factory.create(dummyInitialData)).thenReturn(dummyViewModel to dummyView)

                controller.createView(stage)
                controller.dispose()
                verify(dummyViewModel).dispose()
            }
        }
    }
})
