package com.zlatamigas.testbottomnavigation.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVAdapter
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVModal
import com.zlatamigas.testbottomnavigation.AnimeAPIController
import com.zlatamigas.testbottomnavigation.MainActivity
import com.zlatamigas.testbottomnavigation.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var page = 0

    lateinit var idRVAnimeListFound: RecyclerView
    lateinit var idPBLoadSearchResults: ProgressBar
    lateinit var idIVSearch: ImageView
    lateinit var idTIESearch: TextInputEditText

    lateinit var animeRVModalArrayList: ArrayList<AnimeRVModal>
    lateinit var animeRVAdapter: AnimeRVAdapter
    lateinit var grid: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val controller = this.context?.let { it1 -> AnimeAPIController(it1) }

        idRVAnimeListFound = binding.idRVAnimeListFound
        idPBLoadSearchResults = binding.idPBLoadSearchResults
        idIVSearch = binding.idIVSearch
        idTIESearch = binding.idTIESearch
        grid = binding.grid
        grid.columnCount = 4

        val genresCheckboxes = ArrayList<CheckBox>()
        var check = CheckBox(activity)
        check.text = "Adventure"
        grid.addView(check)
        genresCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "Comedy"
        grid.addView(check)
        genresCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "Mystery"
        grid.addView(check)
        genresCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "Fantasy"
        grid.addView(check)
        genresCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "Horror"
        grid.addView(check)
        genresCheckboxes.add(check)

        val agesCheckboxes = ArrayList<CheckBox>()
        check = CheckBox(activity)
        check.text = "G"
        grid.addView(check)
        agesCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "PG"
        grid.addView(check)
        agesCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "R"
        grid.addView(check)
        agesCheckboxes.add(check)
        check = CheckBox(activity)
        check.text = "R18"
        grid.addView(check)
        agesCheckboxes.add(check)

        animeRVModalArrayList = ArrayList()
        animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)

        idRVAnimeListFound.setAdapter(animeRVAdapter)

        var filterBy = ArrayList<String>()
        var filters = ArrayList<String>()

        idRVAnimeListFound.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)){
                    page++
                    GlobalScope.launch {
                        val animes = controller?.getAnimes(idTIESearch.text.toString(),
                            null, filterBy, filters, page)
                        withContext(Dispatchers.Main) {
                            if (animes != null) {
                                for (anime in animes) {
                                    animeRVModalArrayList.add(
                                        AnimeRVModal(
                                            anime.id,
                                            anime.title,
                                            anime.rating.toString(),
                                            anime.episodeCount.toString(),
                                            anime.posterImage
                                        )
                                    )
                                }
                            }

                            animeRVAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        idIVSearch.setOnClickListener(View.OnClickListener {
            val str = idTIESearch.getText().toString()

            filterBy = ArrayList<String>()
            filters = ArrayList<String>()
            
            if (!str.isEmpty()) {
                idRVAnimeListFound.visibility = View.GONE
                idPBLoadSearchResults.visibility = View.VISIBLE
                var genres = ""
                var ages = ""
                val checkedGenres = genresCheckboxes.filter { cb -> cb.isChecked }
                val checkedAges = agesCheckboxes.filter { cb -> cb.isChecked }
                if (!checkedGenres.isNullOrEmpty()) {
                    filterBy.add("genres")
                    genres = checkedGenres[0].text.toString()
                    for (i in 1 until checkedGenres.size) {
                        genres += ",${checkedGenres[i].text}"
                    }
                    filters.add(genres)
                }

                if (!checkedAges.isNullOrEmpty()) {
                    filterBy.add("ageRating")
                    ages = checkedAges[0].text.toString()
                    for (i in 1 until checkedAges.size) {
                        ages += ",${checkedAges[i].text}"
                    }
                    filters.add(ages)
                }

                page = 0
                GlobalScope.launch {
                    val animes = controller?.getAnimes(
                        idTIESearch.text.toString(),
                        null, filterBy, filters, page
                    )
                    withContext(Dispatchers.Main) {
                        animeRVModalArrayList.clear()
                        if (animes != null) {
                            for (anime in animes) {
                                animeRVModalArrayList.add(
                                    AnimeRVModal(
                                        anime.id,
                                        checkNullString(anime.title),
                                        checkNullString(anime.rating),
                                        checkNullString(anime.episodeCount),
                                        anime.posterImage
                                    )
                                )
                            }
                        }

                        animeRVAdapter.notifyDataSetChanged()
                        idRVAnimeListFound.visibility = View.VISIBLE
                        idPBLoadSearchResults.visibility = View.GONE
                    }
                }
            }
            else {
                Toast.makeText(requireActivity(), "Please enter Anime Title", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        return root
    }

    fun checkNullString(str: Any?): String{
        if(str == null)
            return "--"
        return str.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}