package mateusz.oleksik.remindeme.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.database.DAOs.FoodDAO

@Database(
    entities = [Food::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = AppDatabase.RemoveUserTableMigration::class)],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDAO(): FoodDAO

    @DeleteTable.Entries(value = [DeleteTable(tableName = "User")])
    class RemoveUserTableMigration: AutoMigrationSpec {}
}