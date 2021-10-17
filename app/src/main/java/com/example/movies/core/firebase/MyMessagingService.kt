package com.example.movies.core.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.movies.R
import com.example.movies.data.network.MOVIES_URL
import com.example.movies.presentation.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyMessagingService : FirebaseMessagingService() {

    private val notificationId = 1

    override fun onNewToken(p0: String) {}

    override fun onMessageReceived(p0: RemoteMessage) {
        if (p0.data.isNotEmpty()) {
            sendNotification(p0)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    CHANNEL,
                    CHANNEL, NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = setNotificationBuilder(remoteMessage)

        val notification: Notification = builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(notificationId, notification)
    }

    private fun setNotificationBuilder(remoteMessage: RemoteMessage): NotificationCompat.Builder {
        val data: Map<String, String> = remoteMessage.data
        val title = data[TITLE]
        val content = data[CONTENT]
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        return NotificationCompat.Builder(
            applicationContext,
            CHANNEL
        )
            .setSmallIcon(R.drawable.ic_play_circle_filled_black_24dp)
            .setSound(uri)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle(title)
            .setContentText(content)
            .setCustomContentView(getCollapsedDesign(title, content))
            .setCustomBigContentView(getExpandedDesign())
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
    }

    private fun getCollapsedDesign(title: String?, message: String?): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        return remoteViews
    }

    private fun getExpandedDesign(): RemoteViews {
        val remoteViews =
            RemoteViews(applicationContext.packageName, R.layout.notification_expanded)

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(MOVIES_URL))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, browserIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        remoteViews.setOnClickPendingIntent(R.id.checkout_button, pendingIntent)

        return remoteViews
    }
}


