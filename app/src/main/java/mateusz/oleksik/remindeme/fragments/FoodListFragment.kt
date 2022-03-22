package mateusz.oleksik.remindeme.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.adapters.FoodAdapter
import mateusz.oleksik.remindeme.R
import mateusz.oleksik.remindeme.database.repositories.FoodStorageRepository
import mateusz.oleksik.remindeme.interfaces.FoodItemClickListener

class FoodListFragment : Fragment(), FoodItemClickListener {

    private lateinit var foodStorageRepository: FoodStorageRepository
    private lateinit var foodList: MutableList<Food>
    private lateinit var foodAdapter: FoodAdapter

    companion object {

        fun newInstance(): FoodListFragment {
            return FoodListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context != null){

            Log.i("APPINFO", "Initializing food storage repository in FoodListFragment")
            initRepo(context)
            Log.i("APPINFO", "Initializing food list in FoodListFragment")
            initList()
            Log.i("APPINFO", "Initializing adapter in FoodListFragment")
            initAdapter()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_food_list, container, false)
        val activity = activity as Context
        val recyclerView = view.findViewById<RecyclerView>(R.id.food_list_recycler_view)

        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.adapter = foodAdapter

        return view
    }

    private fun initRepo(context: Context){
        runBlocking {
            foodStorageRepository = FoodStorageRepository(context)
        }
    }

    private fun initList(){
        runBlocking {
            foodList = foodStorageRepository.getAll().toMutableList()
        }
    }

    private fun initAdapter(){
        foodAdapter = FoodAdapter(foodList, this)
    }


    fun addFoodToAdapter(food: Food){
        runBlocking { foodStorageRepository.insertAll(food) }
        foodAdapter.addFood(food);

        Toast.makeText(
            context,
            "Succesfully added new food: ${food.name}",
            Toast.LENGTH_SHORT)
            .show()
    }

    override fun foodItemClicked(position: Int, foodToDelete: Food) {
        runBlocking { foodStorageRepository.delete(foodToDelete) }
        Toast.makeText(
            context,
            "Deletion succesful at postition: ${position}. Name of deleted object: ${foodToDelete.name}",
            Toast.LENGTH_SHORT)
            .show()
    }

}