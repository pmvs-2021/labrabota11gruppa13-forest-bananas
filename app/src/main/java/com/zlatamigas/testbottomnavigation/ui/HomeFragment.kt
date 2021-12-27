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
import com.zlatamigas.testbottomnavigation.databinding.FragmentHomeBinding
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
            } else {
                //TODO
            }
        })

        fillDemoData()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun fillDemoData() {
        animeRVModalArrayList!!.clear()
        for (i in 0 until 10) {
            animeRVModalArrayList.add(
                AnimeRVModal(
                    (i * 19).toString(),
                    "9/10",
                    "13 Eps.",
                    "//cdn-icons-png.flaticon.com/512/4508/4508103.png"
                )
            )
        }
    }
}