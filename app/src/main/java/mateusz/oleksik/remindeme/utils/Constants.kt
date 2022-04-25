package mateusz.oleksik.remindeme.utils

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

        val CreateFoodRequiredPermissions =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
            ).apply {
            }.toTypedArray()
    }
}