package com.zlatamigas.animind.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVAdapter
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVModal
import com.zlatamigas.animind.model.Anime
import com.zlatamigas.animind.controller.api.AnimeAPIController
import com.zlatamigas.animind.MainActivity
import com.zlatamigas.animind.databinding.FragmentFavouritesBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        idRVAnimeListFound = binding.idRVAnimeListUser

        animeRVModalArrayList = ArrayList()
        animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)
        idRVAnimeListFound.setAdapter(animeRVAdapter)

        val dbController = (activity as MainActivity).dbController
        val controller = this.context?.let { it1 -> AnimeAPIController(it1) }

        val favourites = dbController?.getFavourites()


        if (favourites != null) {
            for (favourite in favourites) {

                GlobalScope.launch {

                    var anime: Anime? = null

                    val cm =
                        requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                    if (isConnected) {
                        anime = controller?.getAnime(favourite.id)
                    }
                    else{
                        anime = Anime(
                            favourite.id,
                            favourite.name,
                            "No description (Require Internet connection)",
                            null,
                            null,
                            null,
                            null,
                            null,
                            "h",
                            arrayListOf(),
                            ""
                        )
                    }

                    withContext(Dispatchers.Main) {
                        if (anime != null) {
                            animeRVModalArrayList.add(
                                AnimeRVModal(
                                    anime.id,
                                    anime.title,
                                    checkNullString(anime.rating.toString()),
                                    checkNullString(anime.episodeCount.toString()),
                                    anime.posterImage
                                )
                            )
                        }
                        animeRVAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        return root
    }

    fun checkNullString(str: Any?): String{
        if(str == null)
            return "--"
        if(str.equals("null"))
            return "--"
        return str.toString()
    }

    override fun onResume() {
        super.onResume()
        animeRVAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}