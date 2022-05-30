package mateusz.oleksik.remindme.interfaces

import mateusz.oleksik.remindme.models.Food

interface IFoodCreateDialogListener {
    fun onCreatedFood(food: Food)
}