package mateusz.oleksik.remindeme.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.runBlocking
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.adapters.FoodAdapter
import mateusz.oleksik.remindeme.database.repositories.FoodStorageRepository
import mateusz.oleksik.remindeme.databinding.FragmentFoodListBinding
import mateusz.oleksik.remindeme.interfaces.IFoodItemClickListener
import mateusz.oleksik.remindeme.utils.Constants

class FoodListFragment : Fragment(), IFoodItemClickListener {

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

        Log.i(Constants.DebugLogTag, "Initializing food storage repository in FoodListFragment")
        initRepo(context)
        Log.i(Constants.DebugLogTag, "Initializing food list in FoodListFragment")
        initList()
        Log.i(Constants.DebugLogTag, "Initializing adapter in FoodListFragment")
        initAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentFoodListBinding =
            FragmentFoodListBinding.inflate(inflater, container, false)
        val activity = activity as Context
        val recyclerView = fragmentFoodListBinding.foodListRecyclerView

        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.adapter = foodAdapter

        return fragmentFoodListBinding.root
    }

    private fun initRepo(context: Context) {
        runBlocking {
            foodStorageRepository = FoodStorageRepository(context)
        }
    }

    private fun initList() {
        runBlocking {
            foodList = foodStorageRepository.getAll().toMutableList()
        }
    }

    private fun initAdapter() {
        foodAdapter = FoodAdapter(foodList, this)
    }

    fun addFoodToAdapter(food: Food) {
        runBlocking { foodStorageRepository.insertAll(food) }
        foodAdapter.addFood(food)

        Log.i(
            Constants.InfoLogTag,
            "Successfully added new food: ${food.name}"
        )
    }

    override fun foodItemClicked(position: Int, foodToDelete: Food) {
        runBlocking { foodStorageRepository.delete(foodToDelete) }

        Log.i(
            Constants.InfoLogTag,
            "Deletion successful at position: ${position}. Name of deleted object: ${foodToDelete.name}"
        )
    }
}