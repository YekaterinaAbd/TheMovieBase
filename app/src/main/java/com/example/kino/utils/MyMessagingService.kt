package com.example.kino.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.kino.R
import com.example.kino.model.movie.Movie
import com.example.kino.view.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyMessagingService : FirebaseMessagingService() {

    var movie: Movie? = null

    override fun onNewToken(p0: String) {
        Log.d("TAGGG", p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {

        if (p0.data.isNotEmpty()) {
            sendNotification(p0)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val data: Map<String, String> = remoteMessage.data
        val title = data["title"]
        val content = data["content"]
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channel"

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        //   PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "channel", NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.setSound(uri, null)
            notificationChannel.lightColor = Color.RED

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_play_circle_filled_black_24dp)
                .setSound(uri)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle(title)
                .setContentText(content)
                .setCustomContentView(getCollapsedDesign(title, content))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        notificationManager.notify(1, builder.build())
    }


    private fun getCollapsedDesign(title: String?, message: String?): RemoteViews {

        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.drawable.qualiti)

        return remoteViews
    }

//    private fun getExpandedDesign(title: String?, message: String?): RemoteViews {
//        val remoteViews =
//            RemoteViews(applicationContext.packageName, R.layout.notification_expanded)
//        remoteViews.setTextViewText(R.id.title, title)
//        remoteViews.setTextViewText(R.id.message, message)
//        remoteViews.setImageViewResource(R.id.icon, R.drawable.qualiti)
////        remoteViews.set
////
////
////
////        registrationLink.setOnClickListener {
////            val browserIntent =
////                Intent(Intent.ACTION_VIEW, Uri.parse(SIGN_UP_URL))
////            startActivity(browserIntent)
//        }
//    }


}


