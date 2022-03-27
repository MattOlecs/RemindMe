package mateusz.oleksik.remindeme.interfaces

import mateusz.oleksik.remindeme.models.Food

interface IFoodItemClickListener {
    fun foodItemClicked(position: Int, foodToDelete: Food)
}