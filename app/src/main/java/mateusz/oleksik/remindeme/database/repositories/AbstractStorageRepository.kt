package mateusz.oleksik.remindeme.database.repositories

import android.content.Context
import androidx.room.Room
import mateusz.oleksik.remindeme.database.AppDatabase

abstract class AbstractStorageRepository(context: Context) {
    protected var RemindeMeDb = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "RemindeMeDb"
    ).build()
}