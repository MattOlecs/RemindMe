package mateusz.oleksik.remindeme.database.repositories

import android.content.Context
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.database.DAOs.FoodDAO

class FoodStorageRepository(context: Context) : AbstractStorageRepository(context) {

    private var foodDAO: FoodDAO = RemindeMeDb.foodDAO()

    suspend fun getAll(): List<Food>{
        return foodDAO.getAll()
    }

    suspend fun loadAllByIds(foodIds: IntArray): List<Food>{
        return foodDAO.loadAllByIds(foodIds)
    }

    suspend fun findByName(name: String): Food {
        return foodDAO.findByName(name)
    }

    suspend fun insertAll(vararg foods: Food){
        foodDAO.insertAll(*foods)
    }

    suspend fun delete(food: Food){
        foodDAO.delete(food)
    }
}