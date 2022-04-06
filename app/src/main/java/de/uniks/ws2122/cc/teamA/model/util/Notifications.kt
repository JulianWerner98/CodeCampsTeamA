package de.uniks.ws2122.cc.teamA.model.util

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.R

class Notifications() {

    fun sendNotification(
        notificationId: Int,
        title: String,
        text: String,
        context: Context,
        pendingIntent: PendingIntent
    ) {
        val builder = NotificationCompat.Builder(context, Constant.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)){
            notify(notificationId, builder.build())
        }
    }
}