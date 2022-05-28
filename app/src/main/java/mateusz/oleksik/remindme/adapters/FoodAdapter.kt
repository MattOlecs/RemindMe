package mateusz.oleksik.remindme.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import mateusz.oleksik.remindme.models.Food
import mateusz.oleksik.remindme.databinding.RecyclerFoodItemBinding
import mateusz.oleksik.remindme.interfaces.IFoodItemClickListener
import mateusz.oleksik.remindme.utils.Constants

class FoodAdapter(
    private val foodList: MutableList<Food>,
    private val listener: IFoodItemClickListener
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    init {
        Log.i(Constants.DebugLogTag, "FoodAdapter initialized")
    }

    class FoodViewHolder(view: View, private val recyclerFoodItemBinding: RecyclerFoodItemBinding) :
        RecyclerView.ViewHolder(view) {
        val deleteButton: ImageButton = recyclerFoodItemBinding.deleteFoodButton

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
        val food = foodList[position]
        holder.setData(food)

        holder.deleteButton.setOnClickListener {
            foodList.remove(food)
            listener.foodItemClicked(position, food)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun addFood(food: Food) {
        val oldItemCount = itemCount
        foodList.add(food)
        notifyItemInserted(itemCount)
        notifyItemRangeChanged(oldItemCount, itemCount)
    }
}