package mateusz.oleksik.remindeme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mateusz.oleksik.remindeme.databinding.ActivityMainBinding
import mateusz.oleksik.remindeme.fragments.FoodCreateFragment
import mateusz.oleksik.remindeme.fragments.FoodListFragment
import mateusz.oleksik.remindeme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindeme.utils.Constants

class MainActivity() : AppCompatActivity(), IFoodCreateDialogListener {

    private lateinit var foodListFragment: FoodListFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)

        if (savedInstanceState == null) {

            val fragmentTag = "foodList"
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, FoodListFragment.newInstance(), fragmentTag)
                .commit()
            supportFragmentManager.executePendingTransactions()
            foodListFragment = supportFragmentManager.findFragmentByTag(fragmentTag) as FoodListFragment

            binding.addFoodActionButton.setOnClickListener{
                val dialog = FoodCreateFragment(this)
                dialog.show(supportFragmentManager, "createFoodDialog")
            }
        }
    }

    override fun onCreatedFood(food: Food) {
        Log.i(
            Constants.DebugLogTag,
            "Main activity communicated new food creation to Adapter. Food: ${food.name}")
        foodListFragment.addFoodToAdapter(food)
    }
}