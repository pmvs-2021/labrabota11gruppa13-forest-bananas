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

        idRVAnimeListFound = binding.idRVAnimeListFound
        idIVSearch = binding.idIVSearch
        idTIESearch = binding.idTIESearch


        animeRVModalArrayList = ArrayList()
        animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)

        idRVAnimeListFound.setAdapter(animeRVAdapter)

        idIVSearch.setOnClickListener(View.OnClickListener {
            val str = idTIESearch.getText().toString()
            if (str.isEmpty()) {
                Toast.makeText(requireActivity(), "Please enter Anime Title", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                val controller = this.context?.let { it1 -> AnimeAPIController(it1) }
                GlobalScope.launch {
                    val animes = controller?.getAnimes(
                        idTIESearch.text.toString(),
                        null, null, null, 0
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
                    }
                }
                idRVAnimeListFound.refreshDrawableState()
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