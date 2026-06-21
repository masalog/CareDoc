package org.example.pdfconverter.model

data class Member(
    val insuranceIdNumber: String,
    val name: String,
    val furigana: String,

    val birthYear: Int?,
    val birthMonth: Int?,
    val birthDay: Int?,

    val gender: String,
    val address: String,
    val phone: String,
    val careLevel: String,

    val startYear: Int?,
    val startMonth: Int?,
    val startDay: Int?,

    val endYear: Int?,
    val endMonth: Int?,
    val endDay: Int?,

    val institutionYear: Int?,
    val institutionMonth: Int?,
    val institutionDay: Int?,

    val specificDisease: String?
)