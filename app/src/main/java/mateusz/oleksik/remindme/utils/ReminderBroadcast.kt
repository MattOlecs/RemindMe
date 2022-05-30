package mateusz.oleksik.remindme.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import mateusz.oleksik.remindme.services.NotificationsService
import kotlin.random.Random

class ReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val notificationUtils = NotificationsService(p0!!)
        val title = p1?.getStringExtra(Constants.NotificationExtraTitleHolderName).orEmpty()
        val text = p1?.getStringExtra(Constants.NotificationExtraContentHolderName).orEmpty()

        val builder = notificationUtils.setNotification(title, text)
        notificationUtils.getNotificationManager().notify(Random.nextInt(0, 100), builder.build())
    }
}