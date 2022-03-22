package mateusz.oleksik.remindeme.interfaces

import mateusz.oleksik.remindeme.Food

interface FoodCreateDialogListener {
    fun onCreatedFood(food: Food)
}