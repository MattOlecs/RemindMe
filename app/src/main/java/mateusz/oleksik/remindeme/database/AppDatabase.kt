package mateusz.oleksik.remindeme.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.User
import mateusz.oleksik.remindeme.database.DAOs.FoodDAO
import mateusz.oleksik.remindeme.database.DAOs.UserDAO

@Database(
    entities = [User::class, Food::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun foodDAO(): FoodDAO
}