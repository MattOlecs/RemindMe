package mateusz.oleksik.remindeme.adapters

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.R
import mateusz.oleksik.remindeme.interfaces.FoodItemClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FoodAdapter(
    private val foodsList: MutableList<Food>,
    private val listener: FoodItemClickListener
    ) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    init {
        Log.i("APPINFO", "FoodAdapterV2 initialized")

    }

    class FoodViewHolder(private val view: View) : RecyclerView.ViewHolder(view)  {
        val foodNameTextView: TextView = view.findViewById(R.id.food_list_name_text)
        val foodExpirationTextView: TextView = view.findViewById(R.id.food_list_expiration_date_text)
        val deleteButton: Button = view.findViewById(R.id.delete_food_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_item, parent, false)

        return FoodViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodsList[position]
        holder.foodNameTextView.text = food.name

        //val dateFormatter = DateTimeFormatter.ofPattern("dd-mm-yyyy")
        val date = Date(food.expirationDate)
        holder.foodExpirationTextView.text = DateFormat.format("dd-MM-yyyy", date).toString()

        holder.deleteButton.setOnClickListener(){
            listener.foodItemClicked(position, food)
            foodsList.remove(food)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun getItemCount(): Int {
        return foodsList.size
    }

    fun addFood(food: Food){
        val oldItemCount = itemCount
        foodsList.add(food)
        notifyItemInserted(itemCount)
        notifyItemRangeChanged(oldItemCount, itemCount)
    }
}