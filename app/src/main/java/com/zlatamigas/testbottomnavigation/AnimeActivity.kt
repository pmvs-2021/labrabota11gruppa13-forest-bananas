package com.zlatamigas.testbottomnavigation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AnimeActivity : AppCompatActivity() {

    lateinit var idIVAddFavourite: ImageView
    lateinit var idIVAddNotification: ImageView

    lateinit var idIVAnimeCover: ImageView
    lateinit var idTVAnimeTitle: TextView
    lateinit var idTVAnimeRating: TextView
    lateinit var idTVAnimeYears: TextView
    lateinit var idTVAnimeEpisodes: TextView
    lateinit var idTVAnimeAge: TextView
    lateinit var idTVAnimeGenres: TextView
    lateinit var idTVAnimeDescription: TextView
    lateinit var idPBLoadAnimePage: ProgressBar
    lateinit var idSVAnimeData: ScrollView

    lateinit var dbController: DBController

    var dateAndTime: Calendar = Calendar.getInstance()

    var isFavourite = false

    var idAnime: Int = -1
    var anime: Anime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)

        dbController = DBController(DBHelper(this))

        val extr = intent.extras
        if (extr != null) {
            idAnime = extr.getInt("idAnime")
        }

        idIVAddFavourite = findViewById(R.id.idIVAddFavourite)
        idIVAddNotification = findViewById(R.id.idIVAddNotification)
        idIVAnimeCover = findViewById(R.id.idIVAnimeCover)

        idPBLoadAnimePage = findViewById(R.id.idPBLoadAnimePage)
        idSVAnimeData = findViewById(R.id.idSVAnimeData)

        idTVAnimeTitle = findViewById(R.id.idTVAnimeTitle)
        idTVAnimeRating = findViewById(R.id.idTVAnimeRating)
        idTVAnimeYears = findViewById(R.id.idTVAnimeYears)
        idTVAnimeEpisodes = findViewById(R.id.idTVAnimeEpisodes)
        idTVAnimeAge = findViewById(R.id.idTVAnimeAge)
        idTVAnimeGenres = findViewById(R.id.idTVAnimeGenres)
        idTVAnimeDescription = findViewById(R.id.idTVAnimeDescription)

        isFavourite = dbController.isFavourite(idAnime)

        idIVAddFavourite.setImageResource(
            if (isFavourite) {
                R.drawable.ic_star_selected
            }
            else {
                R.drawable.ic_star_not_selected
            }
        )

        idIVAddFavourite.setOnClickListener {
            isFavourite = !isFavourite

            idIVAddFavourite.setImageResource(
                if (isFavourite) {
                    R.drawable.ic_star_selected
                }
                else {
                    R.drawable.ic_star_not_selected
                }
            )
            if (isFavourite) {
                dbController.addFavourite(idAnime, anime!!.title)
            }
            else {
                dbController.deleteFavourite(idAnime)
            }
        }

        idIVAddNotification.setOnClickListener {

            DatePickerDialog(
                this, dateChoosen,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

        val controller = this.let { it1 -> AnimeAPIController(it1) }
        GlobalScope.launch {
            anime = controller.getAnime(idAnime)
            withContext(Dispatchers.Main) {
                idTVAnimeTitle.setText(checkNullString(anime!!.title))
                idTVAnimeRating.setText("${checkNullString(anime!!.rating)}/100")

                //val input = SimpleDateFormat("ddd MMM dd HH:mm:ss ZZZZ yyyy")
                val output = SimpleDateFormat("MM.yyyy")

                var startDate = "Unknown"
                var endDate = "Still running"
                if (anime!!.startDate != null) {
                    startDate = output.format(anime!!.startDate)
                }
                if (anime!!.endDate != null) {
                    endDate = output.format(anime!!.endDate)
                }

                idTVAnimeYears.setText("${startDate} - ${endDate}")
                idTVAnimeEpisodes.setText(
                    "Episodes: ${checkNullString(anime!!.episodeCount)} (${
                        checkNullString(
                            anime!!.episodeLength
                        )
                    } min)"
                )
                idTVAnimeAge.setText("${checkNullString(anime!!.ageRating)}")
                idTVAnimeGenres.setText("Genres: ")
                for (g in anime!!.genres) {
                    idTVAnimeGenres.append("${checkNullString(g)} ")
                }
                idTVAnimeDescription.setText(checkNullString(anime!!.synopsis))
                Picasso.get().load(anime!!.posterImage).fit().into(idIVAnimeCover)

                idPBLoadAnimePage.visibility = View.GONE
                idSVAnimeData.visibility = View.VISIBLE
            }

        }
    }

    fun checkNullString(str: Any?): String {
        if (str == null)
            return "--"
        return str.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



    // установка обработчика выбора даты
    var dateChoosen =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            dbController.addReminder(anime!!.id, anime!!.title, dateAndTime.time)

            //TODO ADD NOTIFICATION OUT OF APP (ROMAN)
        }
}