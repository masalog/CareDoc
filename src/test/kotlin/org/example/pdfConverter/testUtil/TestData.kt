package org.example.pdfConverter.testUtil

import org.example.pdfConverter.model.*
import java.io.File

object TestData {

    val memberYamada = Member(
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

    val memberSato = memberYamada.copy(
        name = "佐藤絹子",
        furigana = "サトウキヌコ"
    )

    val members = listOf(memberYamada, memberSato)

    val common = CommonData(
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

    val initialData = InitialData(
        members = members,
        common = common,
        templatePdf = File("dummy.pdf")
    )
}