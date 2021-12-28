package com.zlatamigas.pvimslab10_4_v2kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.zlatamigas.testbottomnavigation.AnimeActivity
import com.zlatamigas.testbottomnavigation.R
import java.lang.NumberFormatException
import java.util.ArrayList

class AnimeRVAdapter(
    private val context: Context,
    private val animeRVModalArrayList: ArrayList<AnimeRVModal>
) :
    RecyclerView.Adapter<AnimeRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.anime_rv_item, parent, false)

//        view.setOnClickListener()
//        {
//            val intent = Intent(context, AnimeActivity::class.java)
//            context.startActivity(intent)
//        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = animeRVModalArrayList[position]

        holder.idTV.setText(modal.id.toString())

        holder.ratingTV.setText("${modal.rating}/100")
        holder.titleTV.setText(modal.title)
        holder.episodesTV.setText("${modal.episodes}  Eps.")

        Picasso.get().load(modal.preview.toString()).fit().into(holder.previewIV)
       }

    override fun getItemCount(): Int {
        return animeRVModalArrayList.size
    }

    inner class ViewHolder : RecyclerView.ViewHolder {

        val idTV: TextView

        val titleTV: TextView
        val ratingTV: TextView
        val episodesTV: TextView
        val previewIV: ImageView

        constructor(itemView: View) : super(itemView) {
            itemView.setOnClickListener {
                val intent = Intent(context, AnimeActivity::class.java)
                var idNum: Int = -1

                try {
                    idNum = idTV.text.toString().toInt()
                }
                catch (e:NumberFormatException){
                    Log.d("Id error!", "Incorrect id: ${e.message}")
                }
                intent.putExtra("idAnime", idNum)

                context.startActivity(intent)
            }
        }

        init {
            idTV = itemView.findViewById(R.id.idTVId)
            titleTV = itemView.findViewById(R.id.idTVTitle)
            ratingTV = itemView.findViewById(R.id.idTVRating)
            episodesTV = itemView.findViewById(R.id.idTVEpisodes)
            previewIV = itemView.findViewById(R.id.idIVPreview)
        }
    }
}