package com.zlatamigas.testbottomnavigation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVAdapter
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVModal
import com.zlatamigas.testbottomnavigation.AnimeAPIController
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
    lateinit var idIVSearch: ImageView
    lateinit var idTIESearch: TextInputEditText

    lateinit var animeRVModalArrayList: ArrayList<AnimeRVModal>
    lateinit var animeRVAdapter: AnimeRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val controller = this.context?.let { it1 -> AnimeAPIController(it1) }

        idRVAnimeListFound = binding.idRVAnimeListFound
        idIVSearch = binding.idIVSearch
        idTIESearch = binding.idTIESearch


        animeRVModalArrayList = ArrayList()
        animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)

        idRVAnimeListFound.setAdapter(animeRVAdapter)

        idRVAnimeListFound.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)){
                    page++
                    GlobalScope.launch {
                        val animes = controller?.getAnimes(idTIESearch.text.toString(),
                            null, null, null, page)
                        withContext(Dispatchers.Main) {
                            if (animes != null) {
                                for (anime in animes) {
                                    animeRVModalArrayList.add(
                                        AnimeRVModal(
                                            anime.title,
                                            anime.rating.toString(),
                                            anime.episodeCount.toString(),
                                            anime.posterImage
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })

        idIVSearch.setOnClickListener(View.OnClickListener {
            val str = idTIESearch.getText().toString()
            if (str.isEmpty()) {
                Toast.makeText(requireActivity(), "Please enter Anime Title", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                page = 0
                GlobalScope.launch {
                    val animes = controller?.getAnimes(
                        idTIESearch.text.toString(),
                        null, null, null, page
                    )
                    withContext(Dispatchers.Main) {
                        animeRVModalArrayList.clear()
                        if (animes != null) {
                            for (anime in animes) {
                                animeRVModalArrayList.add(
                                    AnimeRVModal(
                                        checkNullString(anime.title),
                                        checkNullString(anime.rating.toString()),
                                        checkNullString(anime.episodeCount.toString()),
                                        anime.posterImage
                                    )
                                )
                            }
                        }
                    }
                }
            }
        })
        return root
    }

    fun checkNullString(str: String?): String{
        if(str.isNullOrEmpty())
            return ""
        return str
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}