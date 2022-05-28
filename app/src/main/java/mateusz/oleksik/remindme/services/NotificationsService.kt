package mateusz.oleksik.remindme.services

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.app.NotificationCompat
import mateusz.oleksik.remindme.R
import mateusz.oleksik.remindme.utils.Constants
import mateusz.oleksik.remindme.utils.ReminderBroadcast
import kotlin.random.Random

class NotificationsService(context: Context) : ContextWrapper(context) {

    private var _context: Context = context
    private lateinit var _notificationManager: NotificationManager

    init {
        createChannel()
    }

    fun getNotificationManager(): NotificationManager{
        _notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return _notificationManager
    }

    fun setNotification(title: String, content: String): NotificationCompat.Builder{
        return NotificationCompat.Builder(this, Constants.NotificationChannelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_food_salad_icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup("RemindMe")
    }

    fun setReminder(title: String, content: String, timeInMillis: Long) {
        val broadcastIntent = Intent(_context, ReminderBroadcast::class.java)
        broadcastIntent.action = "Extras holder"
        broadcastIntent.putExtra(Constants.NotificationExtraTitleHolderName, title)
        broadcastIntent.putExtra(Constants.NotificationExtraContentHolderName, content)

        val pendingIntent = PendingIntent.getBroadcast(_context, Random.nextInt(), broadcastIntent, PendingIntent.FLAG_ONE_SHOT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    fun cancelReminder() {
        val broadcastIntent = Intent(_context, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(_context, 0, broadcastIntent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            Constants.NotificationChannelId,
            Constants.NotificationTimelineChannelId,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        getNotificationManager().createNotificationChannel(notificationChannel)
    }
}