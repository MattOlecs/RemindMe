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
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.adapters.FoodAdapter
import mateusz.oleksik.remindeme.database.repositories.FoodStorageRepository
import mateusz.oleksik.remindeme.databinding.FragmentFoodListBinding
import mateusz.oleksik.remindeme.interfaces.IFoodItemClickListener
import mateusz.oleksik.remindeme.utils.Constants
import mateusz.oleksik.remindeme.utils.NotificationUtils
import java.util.*

class FoodListFragment : Fragment(), IFoodItemClickListener {

    private lateinit var _foodStorageRepository: FoodStorageRepository
    private lateinit var _foodList: MutableList<Food>
    private lateinit var _foodAdapter: FoodAdapter
    private lateinit var _context: Context

    companion object {

        fun newInstance(): FoodListFragment {
            return FoodListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context

        Log.i(Constants.DebugLogTag, "Initializing food storage repository in FoodListFragment")
        initRepo(context)
        Log.i(Constants.DebugLogTag, "Initializing food list in FoodListFragment")
        initList()
        Log.i(Constants.DebugLogTag, "Initializing adapter in FoodListFragment")
        initAdapter()

        refreshNotifications()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentFoodListBinding =
            FragmentFoodListBinding.inflate(inflater, container, false)

        val recyclerView = fragmentFoodListBinding.foodListRecyclerView
        recyclerView.layoutManager = GridLayoutManager(_context, 1)
        recyclerView.adapter = _foodAdapter

        return fragmentFoodListBinding.root
    }

    fun addFoodToAdapter(food: Food) {
        runBlocking { _foodStorageRepository.insertAll(food) }
        _foodAdapter.addFood(food)

        Log.i(
            Constants.InfoLogTag,
            "Successfully added new food: ${food.name}"
        )
    }

    override fun foodItemClicked(position: Int, foodToDelete: Food) {
        runBlocking { _foodStorageRepository.delete(foodToDelete) }

        Log.i(
            Constants.InfoLogTag,
            "Deletion successful at position: ${position}. Name of deleted object: ${foodToDelete.name}"
        )
    }

    private fun initRepo(context: Context) {
        runBlocking {
            _foodStorageRepository = FoodStorageRepository(context)
        }
    }

    private fun initList() {
        runBlocking {
            _foodList = _foodStorageRepository.getAll().toMutableList()
        }
    }

    private fun initAdapter() {
        _foodAdapter = FoodAdapter(_foodList, this)
    }

    private fun refreshNotifications(){
        val notificationUtils = NotificationUtils(_context)
        notificationUtils.cancelReminder()

        for (food in _foodList){
            val restrictionDate = Calendar.getInstance()
            restrictionDate.set(Calendar.HOUR_OF_DAY, 0)
            restrictionDate.set(Calendar.MINUTE, 0)

            val foodExpirationDate = Calendar.getInstance();
            foodExpirationDate.timeInMillis = food.expirationDate

            if (foodExpirationDate.time < restrictionDate.time){
                continue;
            }

            restrictionDate.add(Calendar.DATE, 1)

            if (foodExpirationDate.time > restrictionDate.time){
                continue;
            }

            foodExpirationDate.set(Calendar.HOUR_OF_DAY, 18)
            foodExpirationDate.set(Calendar.MINUTE, 30)

            notificationUtils.setReminder(
                "Don't waste!",
                "${food.name} is going to expire soon!",
                foodExpirationDate.timeInMillis)
        }
    }
}