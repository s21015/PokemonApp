package jp.ac.it_college.std.s21015.jobapp

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonName(
    val id: String,
)