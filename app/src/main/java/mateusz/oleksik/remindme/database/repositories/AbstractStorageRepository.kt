package mateusz.oleksik.remindme.database.repositories

import android.content.Context
import androidx.room.Room
import mateusz.oleksik.remindme.database.AppDatabase

abstract class AbstractStorageRepository(context: Context) {
    protected var dbAccess = AppDatabase.getDatabaseAccess(context)
}