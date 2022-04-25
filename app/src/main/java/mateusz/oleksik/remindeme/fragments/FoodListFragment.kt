package mateusz.oleksik.remindeme.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.runBlocking
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.adapters.FoodAdapter
import mateusz.oleksik.remindeme.database.repositories.FoodStorageRepository
import mateusz.oleksik.remindeme.databinding.FragmentFoodListBinding
import mateusz.oleksik.remindeme.interfaces.IFoodItemClickListener
import mateusz.oleksik.remindeme.utils.Constants
import mateusz.oleksik.remindeme.utils.Extensions.Companion.compareDateMonthYear
import mateusz.oleksik.remindeme.services.NotificationsService
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
        refreshNotifications()

        Log.i(
            Constants.InfoLogTag,
            "Successfully added new food: ${food.name}"
        )
    }

    override fun foodItemClicked(position: Int, foodToDelete: Food) {
        runBlocking { _foodStorageRepository.delete(foodToDelete) }
        refreshNotifications()

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
        val notificationUtils = NotificationsService(_context)
        notificationUtils.cancelReminder()

        val notificationHour =
            PreferenceManager
                .getDefaultSharedPreferences(_context)
                .getString("notification_hour", "16")
                .toString()
                .toIntOrNull() ?: 16
        val notificationMinute =
            PreferenceManager
                .getDefaultSharedPreferences(_context)
                .getString("notification_minute", "0")
                .toString()
                .toIntOrNull() ?: 0

        var currentTime = Calendar.getInstance()
        val notificationTime = Calendar
            .getInstance()

        notificationTime.set(Calendar.HOUR_OF_DAY, notificationHour)
        notificationTime.set(Calendar.MINUTE, notificationMinute)

        if (currentTime.time > notificationTime.time){
            return
        }

        for (food in _foodList){
            val foodExpirationDate = Calendar.getInstance()
            foodExpirationDate.timeInMillis = food.expirationDate
            currentTime = Calendar.getInstance()

            foodExpirationDate.set(Calendar.HOUR_OF_DAY, 0)
            foodExpirationDate.set(Calendar.MINUTE, 0)
            foodExpirationDate.set(Calendar.SECOND, 0)

            currentTime.set(Calendar.HOUR_OF_DAY, 0)
            currentTime.set(Calendar.MINUTE, 0)
            currentTime.set(Calendar.SECOND, 0)

            if (!foodExpirationDate.compareDateMonthYear(currentTime)){
                continue
            }

            currentTime.add(Calendar.DATE, 1)
            if (foodExpirationDate.time > currentTime.time){
                continue
            }

            notificationUtils.setReminder(
                "Don't waste!",
                "${food.name} is going to expire soon!",
                notificationTime.timeInMillis)
        }
    }
}