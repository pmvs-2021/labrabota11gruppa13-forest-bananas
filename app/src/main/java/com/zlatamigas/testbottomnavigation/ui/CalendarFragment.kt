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
import java.text.SimpleDateFormat
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

            val date = Calendar.getInstance()

            date.set(Calendar.YEAR, year)
            date.set(Calendar.MONTH, month)
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val output = SimpleDateFormat("dd.MM.yyyy")
            val nowDate = output.format(date.time)

            val dateReminders = reminders?.filter { r ->
                val temp = output.format(r.reminderDate)
                temp.equals(nowDate) }

            animeRVModalArrayList.clear()
            animeRVAdapter.notifyDataSetChanged()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}