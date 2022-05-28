package mateusz.oleksik.remindme.interfaces

import mateusz.oleksik.remindme.models.Food

interface IFoodItemClickListener {
    fun foodItemClicked(position: Int, foodToDelete: Food)
}