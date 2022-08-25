package jp.ac.it_college.std.s21015.jobapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import jp.ac.it_college.std.s21015.jobapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://pokeapi.co/"
private const val ICON_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%s.png"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private var pokemonList: List<Pokemon>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPokemonList()

        initListViewEvent()
    }

    private fun initPokemonList() {
        val jsonStr = resources.assets.open("pokemon_list.json").reader().readText()
        val type = Types.newParameterizedType(List::class.java, Pokemon::class.java)
        val adapter: JsonAdapter<List<Pokemon>> = moshi.adapter(type)
        pokemonList = adapter.fromJson(jsonStr)
        val nameList = pokemonList?.map { mapOf("name" to it.name) }
        binding.pokemonListView.adapter = SimpleAdapter(
            this@MainActivity, nameList,
            android.R.layout.simple_list_item_1,
            arrayOf("name"), intArrayOf(android.R.id.text1)
        )
    }
    private fun initListViewEvent() {
        binding.pokemonListView.setOnItemClickListener { _, _, position, _ ->
            pokemonList?.let { list ->
                pokemonName(list[position].num)
            }
        }
    }

    @UiThread
    private fun pokemonName(pokemon: String) {
        lifecycleScope.launch {
            val info = getPokemonName(pokemon)
            setPokemonName(info)
        }
    }

    @WorkerThread
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getPokemonName(pokemon: String): PokemonName {
        return withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder().apply {
                baseUrl(BASE_URL)
                addConverterFactory(MoshiConverterFactory.create(moshi))
            }.build()
            val service: PokemonService = retrofit.create(PokemonService::class.java)
            try {
                service.fetchPokemon(pokemonNum = pokemon).execute().body()
                    ?: throw IllegalStateException("ポケモン情報が取れません")
            } catch (e: Exception) {
                throw IllegalStateException("例外が発生しました。", e)
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    @UiThread
    private fun setPokemonName(info: PokemonName) {
        Picasso.get().load(ICON_URL.format(info.id))
            .into(binding.pokemonIcon)
    }
}