package com.zlatamigas.testbottomnavigation.ui

import android.R
import android.R.attr
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
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
import android.widget.RadioButton

import android.widget.RadioGroup
import androidx.core.view.marginBottom
import android.R.attr.right

import android.R.attr.left
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo

import android.widget.LinearLayout


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
    lateinit var linear: LinearLayout

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
        linear = binding.idLLFilter

        var filterBy = ArrayList<String>()
        var filters = ArrayList<String>()
        var sortBy: String? = "averageRating"

        var grid = GridLayout(activity)
        grid.columnCount = 5


        var lable = TextView(activity)
        lable.text = "Genres:"
        lable.setTextColor(resources.getColor(R.color.white))
        linear.addView(lable)


        val genresCheckboxes = ArrayList<CheckBox>()
        var check = CheckBox(activity)
        val genresArray = arrayOf(
            "Adventure", "Comedy", "Mystery", "Action",
            "Horror", "Drama", "Magic", "School",
            "Fantasy", "Sports", "Romance", "Music",
            "Thriller", "Supernatural", "Historical"
        )
        for (gn in genresArray) {
            check = CheckBox(activity)
            check.text = gn
            check.setTextColor(resources.getColor(R.color.white))
            grid.addView(check)
            genresCheckboxes.add(check)
        }
        linear.addView(grid)

        grid = GridLayout(activity)
        grid.columnCount = 4
        lable = TextView(activity)
        lable.text = "Age ratings:"
        lable.setTextColor(resources.getColor(R.color.white))
        linear.addView(lable)

        val agesCheckboxes = ArrayList<CheckBox>()
        check = CheckBox(activity)
        val ageArray = arrayOf("G", "PG", "R", "R18")
        for (an in ageArray) {
            check = CheckBox(activity)
            check.text = an
            check.setTextColor(resources.getColor(R.color.white))
            grid.addView(check)
            agesCheckboxes.add(check)
        }
        linear.addView(grid)


        animeRVModalArrayList = ArrayList()
        animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)
        idRVAnimeListFound.setAdapter(animeRVAdapter)


        idRVAnimeListFound.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page++
                    GlobalScope.launch {
                        val animes = controller?.getAnimes(
                            idTIESearch.text.toString(),
                            sortBy, filterBy, filters, page
                        )
                        withContext(Dispatchers.Main) {
                            if (animes != null) {
                                for (anime in animes) {
                                    animeRVModalArrayList.add(
                                        AnimeRVModal(
                                            anime.id,
                                            checkNullString(anime.title),
                                            checkNullString(anime.rating.toString()),
                                            checkNullString(anime.episodeCount.toString()),
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

            page = 0

            val cm =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (!isConnected) {
                Toast.makeText(requireContext(), "Check Internet Connection..", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            val str = idTIESearch.text.toString()
            if (str.isNullOrEmpty())
                sortBy = "averageRating"
            else
                sortBy = null

            filterBy = ArrayList<String>()
            filters = ArrayList<String>()

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

            GlobalScope.launch {
                val animes = controller?.getAnimes(
                    str,
                    sortBy, filterBy, filters, page
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
        })

        return root
    }

    fun checkNullString(str: Any?): String {
        if (str == null)
            return "--"
        if (str.equals("null"))
            return "--"
        return str.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}