package mateusz.oleksik.remindme.utils

import android.Manifest

class Constants {
    companion object{
        const val DebugLogTag = "DBG"
        const val InfoLogTag = "INF"
        const val CreateFoodPermissionsRequestCode = 10
        const val NotificationChannelId = "Main channel"
        const val NotificationTimelineChannelId = "Main timeline channel"
        const val NotificationExtraTitleHolderName = "NotificationTitle"
        const val NotificationExtraContentHolderName = "NotificationContent"
        const val PreferencesNotificationHourStringName = "notification_hour"
        const val PreferencesNotificationMinuteStringName = "notification_minute"
        const val DateDetectionPatternRegex = """(0?[1-9]|[12][0-9]|3[01])[- /.:](0?[1-9]|1[012])[- /.:](19|20)\d\d"""

        val CreateFoodRequiredPermissions =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
            ).apply {
            }.toTypedArray()
    }
}