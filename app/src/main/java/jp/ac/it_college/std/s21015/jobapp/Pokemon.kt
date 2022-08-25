package jp.ac.it_college.std.s21015.jobapp

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Pokemon(
    val name: String,
    val num: String
)
