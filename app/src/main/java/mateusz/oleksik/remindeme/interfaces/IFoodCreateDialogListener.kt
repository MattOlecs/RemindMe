package mateusz.oleksik.remindeme.interfaces

import mateusz.oleksik.remindeme.Food

interface IFoodCreateDialogListener {
    fun onCreatedFood(food: Food)
}