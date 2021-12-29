package com.zlatamigas.animind

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.zlatamigas.animind.controller.api.AnimeAPIController
import com.zlatamigas.animind.controller.db.DBController
import com.zlatamigas.animind.controller.db.DBHelper
import com.zlatamigas.animind.model.Anime
import com.zlatamigas.animind.controller.notification.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AnimeActivity : AppCompatActivity() {

    private lateinit var idIVAddFavourite: ImageView
    private lateinit var idIVAddNotification: ImageView
    private lateinit var idIVAnimeCover: ImageView

    private lateinit var idTVAnimeTitle: TextView
    private lateinit var idTVAnimeRating: TextView
    private lateinit var idTVAnimeYears: TextView
    private lateinit var idTVAnimeEpisodes: TextView
    private lateinit var idTVAnimeAge: TextView
    private lateinit var idTVAnimeGenres: TextView
    private lateinit var idTVAnimeDescription: TextView

    private lateinit var idPBLoadAnimePage: ProgressBar
    private lateinit var idSVAnimeData: ScrollView

    private lateinit var dbController: DBController

    private var dateAndTime: Calendar = Calendar.getInstance()

    private var isFavourite = false

    private var idAnime: Int = -1
    private var anime: Anime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        createNotificationChannel()


        dbController = DBController(DBHelper(this))

        idAnime = intent.extras?.getInt("idAnime") ?: -1

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
            when {
                isFavourite -> R.drawable.ic_star_selected
                else -> R.drawable.ic_star_not_selected
            }
        )

        idIVAddFavourite.setOnClickListener {
            isFavourite = !isFavourite

            idIVAddFavourite.setImageResource(
                when {
                    isFavourite -> R.drawable.ic_star_selected
                    else -> R.drawable.ic_star_not_selected
                }
            )

            when {
                isFavourite -> dbController.addFavourite(idAnime, anime!!.title)
                else -> dbController.deleteFavourite(idAnime)
            }
        }

        idIVAddNotification.setOnClickListener {

            DatePickerDialog(
                this, dateChosen,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
            Toast.makeText(applicationContext, "Require Internet Connection", Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        val controller = AnimeAPIController(this)
        GlobalScope.launch {

            anime = controller.getAnime(idAnime)

            withContext(Dispatchers.Main) {
                idTVAnimeTitle.text = checkNullString(anime!!.title)
                idTVAnimeRating.text = "${checkNullString(anime!!.rating)}/100"

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

                idTVAnimeYears.text = "$startDate - $endDate"
                idTVAnimeEpisodes.text =
                    "Episodes: ${checkNullString(anime!!.episodeCount)} (${checkNullString(anime!!.episodeLength)} min)"
                idTVAnimeAge.text = "${checkNullString(anime!!.ageRating)}"
                idTVAnimeGenres.text = "Genres: "

                anime!!.genres.forEach { idTVAnimeGenres.append("${checkNullString(it)} ") }

                idTVAnimeDescription.text = checkNullString(anime!!.synopsis)
                Picasso.get().load(anime!!.posterImage).fit().into(idIVAnimeCover)

                idPBLoadAnimePage.visibility = View.GONE
                idSVAnimeData.visibility = View.VISIBLE
            }
        }
    }

    private fun checkNullString(str: Any?): String = when (str) {
        null -> "--"
        else -> str.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // установка обработчика выбора даты
    private var dateChosen =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val title = anime!!.title
            val date = dateAndTime.time
            val synopsis = anime!!.synopsis
            val imageUrl = anime!!.posterImage
            dbController.addReminder(anime!!.id, title, date)

            scheduleNotification(title, date, synopsis, imageUrl)

            AlertDialog.Builder(this)
                .setTitle("Well Done!")
                .setMessage(
                    "Your notification added!!" +
                            "\nTitle: " + title +
                            "\nAt: " + date
                )
                .setPositiveButton("Okay") { _, _ -> }
                .show()
        }

    private fun scheduleNotification(
        title: String,
        date: Date,
        synopsis: String,
        imageUrl: String
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val intent = Intent(applicationContext, AnimeNotification::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(bigText, synopsis)
        intent.putExtra(bigImage, imageUrl)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR, 12)
        // TODO(Сделать на релизе calendar.timeInMillis)
        val time = System.currentTimeMillis() + 5000

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            channelID,
            "Notification Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "A Description of the Channel"
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}