package jp.ac.it_college.std.s21015.jobapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {
    @GET("api/v2/pokemon-form/{id}")
    fun fetchPokemon(
        @Path("id") pokemonNum: String,
    ): Call<PokemonName>
}