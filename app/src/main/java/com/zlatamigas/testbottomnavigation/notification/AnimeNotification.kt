package com.zlatamigas.testbottomnavigation.notification

import com.zlatamigas.testbottomnavigation.R

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.graphics.BitmapFactory
import java.util.*
import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target


const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "Tap to view your anime plans..."
const val bigText = "bigText"
const val bigImage = "url"

class AnimeNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // немного читерства...
        // TODO(убрать на релизе)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        Picasso.get().load(intent.getStringExtra(bigImage))
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom?) {
                    val notification = NotificationCompat.Builder(context, channelID)
                        .setContentIntent(pendingIntent)//убрать потом
                        .setSmallIcon(R.drawable.ic_app_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(context.resources, R.drawable.ic_app_icon)
                        )
                        .setContentText(messageExtra)
                        .setContentTitle(intent.getStringExtra(titleExtra))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .setSummaryText(intent.getStringExtra(bigText))
                        )
                        .build()

                    val manager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val randID = (Date().time / 1000L % Int.MAX_VALUE).toInt()
                    manager.notify(randID, notification)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }
}