package mateusz.oleksik.remindme.database.repositories

import android.content.Context
import mateusz.oleksik.remindme.models.Food
import mateusz.oleksik.remindme.database.DAOs.FoodDAO

class FoodStorageRepository(context: Context) : AbstractStorageRepository(context) {

    private var foodDAO: FoodDAO? = dbAccess?.foodDAO()

    suspend fun getAll(): List<Food>?{
        return foodDAO?.getAll()
    }

    suspend fun loadAllByIds(foodIds: IntArray): List<Food>?{
        return foodDAO?.loadAllByIds(foodIds)
    }

    suspend fun findByName(name: String): Food? {
        return foodDAO?.findByName(name)
    }

    suspend fun insertAll(vararg foods: Food){
        foodDAO?.insertAll(*foods)
    }

    suspend fun delete(food: Food){
        foodDAO?.delete(food)
    }
}