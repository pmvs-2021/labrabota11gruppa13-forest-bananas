package com.zlatamigas.testbottomnavigation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVModal
import com.zlatamigas.testbottomnavigation.AnimeAPIController
import com.zlatamigas.testbottomnavigation.MainActivity
import com.zlatamigas.testbottomnavigation.databinding.FragmentCalendarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

import android.widget.CalendarView

import android.R
import android.content.Context
import com.zlatamigas.pvimslab10_4_v2kotlin.AnimeRVAdapter
import java.lang.StringBuilder
import java.util.*


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val dbController = (activity as MainActivity).dbController
        val controller = this.context?.let { it1 -> AnimeAPIController(it1) }

        val reminders = dbController?.getReminders()

        val calendarView = binding.calendarView
        val idRVAnimeListFound = binding.idRVAnimeListReminders

        val animeRVModalArrayList = ArrayList<AnimeRVModal>()
        val animeRVAdapter = AnimeRVAdapter(requireActivity(), animeRVModalArrayList!!)
        idRVAnimeListFound.setAdapter(animeRVAdapter)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = Date(year, month, dayOfMonth)

            val dateReminders = reminders?.filter { r -> r.reminderDate == date }

            if (dateReminders != null) {
                for (favourite in dateReminders) {
                    GlobalScope.launch {
                        val anime = controller?.getAnime(favourite.id)
                        withContext(Dispatchers.Main) {
                            if (anime != null) {
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
                            animeRVAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}