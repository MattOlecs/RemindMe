package mateusz.oleksik.remindeme.obsolete

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mateusz.oleksik.remindeme.database.repositories.FoodStorageRepository
import kotlinx.coroutines.runBlocking
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.R

class `FoodAdapter-obsolete`(
    private val context: Context,
    ) : RecyclerView.Adapter<`FoodAdapter-obsolete`.FoodViewHolder>() {

    private lateinit var foodStorageRepository: FoodStorageRepository;
    private var foodsList: MutableList<Food> = emptyList<Food>().toMutableList()

    init {
        Log.i("APPINFO", "Inicjalizacja FoodAdapter")
        initRepo()
        refreshList()
        Log.i("APPINFO", "Lista ma ${foodsList.size} elementów")
    }

    class FoodViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
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
        holder.foodExpirationTextView.text = food.expirationDate.toString()

        holder.deleteButton.setOnClickListener(){

            runBlocking { foodStorageRepository.delete(food) }
            refreshList()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun getItemCount(): Int {
        return foodsList.size
    }

    private fun initRepo(){
        runBlocking {
            foodStorageRepository = FoodStorageRepository(context)
        }
    }

    private fun refreshList(){
        runBlocking {
            foodsList = foodStorageRepository.getAll().toMutableList()
        }
    }
}