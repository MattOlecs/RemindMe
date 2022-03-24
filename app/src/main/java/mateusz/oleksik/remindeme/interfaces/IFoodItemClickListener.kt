package mateusz.oleksik.remindeme.interfaces

import mateusz.oleksik.remindeme.Food

interface IFoodItemClickListener {
    fun foodItemClicked(position: Int, foodToDelete: Food)
}