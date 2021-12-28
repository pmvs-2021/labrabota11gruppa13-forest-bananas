package com.zlatamigas.testbottomnavigation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVAdapter
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVModal
import com.zlatamigas.testbottomnavigation.AnimeAPIController
import com.zlatamigas.testbottomnavigation.MainActivity
import com.zlatamigas.testbottomnavigation.databinding.FragmentFavouritesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    lateinit var animeRVModalArrayList: ArrayList<AnimeRVModal>
    lateinit var animeRVAdapter: AnimeRVAdapter

    lateinit var idRVAnimeListFound: RecyclerView
    lateinit var idSPFilter: Spinner
    lateinit var idIVFilter: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        idIVFilter = binding.idIVFilter
        idRVAnimeListFound = binding.idRVAnimeListUser
        idSPFilter = binding.idSPFilter

        animeRVModalArrayList = ArrayList()
        animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)

        idRVAnimeListFound.setAdapter(animeRVAdapter)

        idIVFilter.setOnClickListener(View.OnClickListener {
            //TODO
        })
        val dbController = (activity as MainActivity).dbController
        val controller = this.context?.let { it1 -> AnimeAPIController(it1) }

        val favourites = dbController?.getFavourites()
        if (favourites != null) {
            for (favourite in favourites) {
                GlobalScope.launch {
                    val anime = controller?.getAnime(favourite.id)
                    withContext(Dispatchers.Main) {
                        if (anime != null) {
                            AnimeRVModal(
                                anime.title, 
                                anime.rating.toString(),
                                anime.episodeCount.toString(),
                                anime.posterImage
                            )
                        }
                    }
                }
            }
        }
        //fillDemoData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun fillDemoData() {
        animeRVModalArrayList!!.clear()
        for (i in 0 until 3) {
            animeRVModalArrayList.add(
                AnimeRVModal(
                    (i * 45).toString(),
                    "10/10",
                    "133 Eps.",
                    "//cdn-icons-png.flaticon.com/512/4508/4508103.png"
                )
            )
        }
    }
}