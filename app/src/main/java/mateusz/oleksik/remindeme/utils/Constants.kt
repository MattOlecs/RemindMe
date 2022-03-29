package mateusz.oleksik.remindeme.utils

import android.Manifest

class Constants {
    companion object{
        const val DebugLogTag = "DBG"
        const val InfoLogTag = "INF"
        const val CreateFoodPermissionsRequestCode = 10

        val CreateFoodRequiredPermissions =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
            ).apply {
            }.toTypedArray()
    }
}