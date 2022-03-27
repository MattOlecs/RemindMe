package mateusz.oleksik.remindeme.interfaces

import mateusz.oleksik.remindeme.models.Food

interface IFoodCreateDialogListener {
    fun onCreatedFood(food: Food)
}