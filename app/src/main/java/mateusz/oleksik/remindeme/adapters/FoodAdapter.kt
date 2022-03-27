package mateusz.oleksik.remindeme.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.databinding.RecyclerFoodItemBinding
import mateusz.oleksik.remindeme.interfaces.IFoodItemClickListener
import mateusz.oleksik.remindeme.utils.Constants

class FoodAdapter(
    private val foodsList: MutableList<Food>,
    private val listenerI: IFoodItemClickListener
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    init {
        Log.i(Constants.DebugLogTag, "FoodAdapter initialized")
    }

    class FoodViewHolder(view: View, private val recyclerFoodItemBinding: RecyclerFoodItemBinding) :
        RecyclerView.ViewHolder(view) {
        val deleteButton: Button = recyclerFoodItemBinding.deleteFoodButton

        fun setData(food: Food) {
            recyclerFoodItemBinding.food = food
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val recyclerFoodItemBinding =
            RecyclerFoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(recyclerFoodItemBinding.root, recyclerFoodItemBinding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodsList[position]
        holder.setData(food)

        holder.deleteButton.setOnClickListener {
            listenerI.foodItemClicked(position, food)
            foodsList.remove(food)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun getItemCount(): Int {
        return foodsList.size
    }

    fun addFood(food: Food) {
        val oldItemCount = itemCount
        foodsList.add(food)
        notifyItemInserted(itemCount)
        notifyItemRangeChanged(oldItemCount, itemCount)
    }
}