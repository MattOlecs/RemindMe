package mateusz.oleksik.remindme.services

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import mateusz.oleksik.remindme.R
import mateusz.oleksik.remindme.utils.Constants
import mateusz.oleksik.remindme.utils.ReminderBroadcast
import org.json.JSONArray

class NotificationsService(context: Context) : ContextWrapper(context) {

    private var _context: Context = context
    private var _alarmsTag = ":alarm"
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

    fun setReminder(title: String, content: String, timeInMillis: Long, id: Int) {
        try {
            val broadcastIntent = Intent(_context, ReminderBroadcast::class.java)
            broadcastIntent.action = "Extras holder"
            broadcastIntent.putExtra(Constants.NotificationExtraTitleHolderName, title)
            broadcastIntent.putExtra(Constants.NotificationExtraContentHolderName, content)

            val pendingIntent = PendingIntent.getBroadcast(_context, id, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

            saveAlarmId(id)
        } catch (e: Exception) {
            AlertDialog.Builder(_context)
                .setTitle("Creating reminder failed!")
                .setMessage(e.localizedMessage)
        }
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

    fun cancelAllAlarms() {
        try {
            for (alarmId in getAlarmIds()) {
                cancelAlarm(alarmId)
            }
        } catch (e: Exception) {
            AlertDialog.Builder(_context)
                .setTitle("Cancelling previous notifications failed!")
                .setMessage(e.localizedMessage)
        }
    }

    private fun cancelAlarm(notificationId: Int) {
        val alarmManager = _context.getSystemService(ALARM_SERVICE) as AlarmManager
        val broadcastIntent = Intent(_context, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            _context,
            notificationId,
            broadcastIntent,
            PendingIntent.FLAG_CANCEL_CURRENT)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        removeAlarmId(notificationId)
    }

    private fun removeAlarmId(id: Int) {
        val idsAlarms: MutableList<Int> = getAlarmIds()
        idsAlarms.removeIf { x -> x == id }

        saveIdsInPreferences(idsAlarms)
    }

    private fun saveAlarmId(id: Int) {
        val idsAlarms: MutableList<Int> = getAlarmIds()
        if (idsAlarms.contains(id)) {
            return
        }
        idsAlarms.add(id)
        saveIdsInPreferences(idsAlarms)
    }

    private fun getAlarmIds(): MutableList<Int> {
        val ids: MutableList<Int> = ArrayList()
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(_context)
            val jsonArray2 = JSONArray(prefs.getString(_context.packageName + _alarmsTag, "[]"))
            for (i in 0 until jsonArray2.length()) {
                ids.add(jsonArray2.getInt(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ids
    }

    private fun saveIdsInPreferences(lstIds: List<Int>) {
        val jsonArray = JSONArray()
        for (idAlarm in lstIds) {
            jsonArray.put(idAlarm)
        }
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context)
        val editor = prefs.edit()
        editor.putString(_context.packageName + _alarmsTag, jsonArray.toString())
        editor.apply()
    }
}