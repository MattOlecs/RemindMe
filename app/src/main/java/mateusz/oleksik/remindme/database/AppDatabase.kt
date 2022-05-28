package mateusz.oleksik.remindme.database

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import mateusz.oleksik.remindme.models.Food
import mateusz.oleksik.remindme.database.DAOs.FoodDAO

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
    companion object{

        private var databaseAccess: AppDatabase? = null

        @Synchronized
        fun getDatabaseAccess(context: Context) : AppDatabase?{

            if(databaseAccess == null) {

                try {
                    val callbacks = object : Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                        }
                    }

                    databaseAccess =  Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "RemindeMeDb"
                    )
                        .addCallback(callbacks)
                        .build()
                }
                catch (e: Exception)
                {
                    AlertDialog.Builder(context).setMessage(e.localizedMessage).show()
                }
            }
            return  databaseAccess
        }
    }

    @DeleteTable.Entries(value = [DeleteTable(tableName = "User")])
    class RemoveUserTableMigration: AutoMigrationSpec {}
}