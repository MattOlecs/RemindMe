package mateusz.oleksik.remindeme.database.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.User

@Dao
interface FoodDAO {
    @Query("SELECT * FROM food")
    suspend fun getAll(): List<Food>

    @Query("SELECT * FROM food WHERE id IN (:foodIds)")
    suspend fun loadAllByIds(foodIds: IntArray): List<Food>

    @Query("SELECT * FROM food WHERE name LIKE :name")
    suspend fun findByName(name: String): Food

    @Insert
    suspend fun insertAll(vararg foods: Food)

    @Delete
    suspend fun delete(food: Food)
}