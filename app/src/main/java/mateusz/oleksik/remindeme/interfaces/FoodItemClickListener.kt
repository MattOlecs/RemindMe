package mateusz.oleksik.remindeme.interfaces

import mateusz.oleksik.remindeme.Food

interface FoodItemClickListener {
    fun foodItemClicked(position: Int, foodToDelete: Food)
}