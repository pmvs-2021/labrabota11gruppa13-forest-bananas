package com.zlatamigas.testbottomnavigation

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

    var isFavourite = false
    var isNotified = false

    var idAnime: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)

        //Receive data from bundle about depicted element
        val extr = intent.extras
        if(extr!=null){
            idAnime = extr.getInt("idAnime")
            Toast.makeText(this, idAnime.toString(), Toast.LENGTH_SHORT).show()
        }


        idIVAddFavourite = findViewById(R.id.idIVAddFavourite)
        idIVAddNotification = findViewById(R.id.idIVAddNotification)
        idIVAnimeCover = findViewById(R.id.idIVAnimeCover)

        idTVAnimeTitle = findViewById(R.id.idTVAnimeTitle)
        idTVAnimeRating = findViewById(R.id.idTVAnimeRating)
        idTVAnimeYears = findViewById(R.id.idTVAnimeYears)
        idTVAnimeEpisodes = findViewById(R.id.idTVAnimeEpisodes)
        idTVAnimeAge = findViewById(R.id.idTVAnimeAge)
        idTVAnimeGenres = findViewById(R.id.idTVAnimeGenres)
        idTVAnimeDescription = findViewById(R.id.idTVAnimeDescription)

        idIVAddFavourite.setOnClickListener {
            idIVAddFavourite.setImageResource(
                if (isFavourite) {
                    R.drawable.ic_star_not_selected
                } else {
                    R.drawable.ic_star_selected
                }
            )
            isFavourite = !isFavourite
        }

        idIVAddNotification.setOnClickListener {
            idIVAddNotification.setImageResource(
                if (isNotified) {
                    R.drawable.ic_notify_off
                } else {
                    R.drawable.ic_notify_on
                }
            )
            isNotified = !isNotified
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}