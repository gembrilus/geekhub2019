package iv.nakonechnyi.newsfeeder.util

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import iv.nakonechnyi.newsfeeder.R

private var _NOTE_ID = 0
private val NOTE_ID = _NOTE_ID++

fun makeSimpleNotification(context: Context,
                           title: String,
                           text: String) {

    val note = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_stat_name)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    with(NotificationManagerCompat.from(context)){
        notify(NOTE_ID, note.build())
    }
}

fun makeForActionNotification(context: Context,
                                title: String,
                                bigText: String,
                                intent: PendingIntent? = null){
    val note = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_stat_name)
        .setContentTitle(title)
        .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(intent)
        .setShowWhen(false)
        .setAutoCancel(true)
    with(NotificationManagerCompat.from(context)){
        notify(NOTE_ID, note.build())
    }
}