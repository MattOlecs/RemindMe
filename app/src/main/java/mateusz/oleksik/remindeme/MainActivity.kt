package mateusz.oleksik.remindeme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.gms.dynamic.SupportFragmentWrapper
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            generateFoodList()

            binding.addFoodActionButton.setOnClickListener {
                openCreateFoodDialog()
            }
        }
    }

    private fun generateFoodList() {
        val fragmentTag = "foodList"
        supportFragmentManager
            .beginTransaction()
            .add(R.id.root_layout, FoodListFragment.newInstance(), fragmentTag)
            .commit()
        supportFragmentManager.executePendingTransactions()

        foodListFragment =
            supportFragmentManager.findFragmentByTag(fragmentTag) as FoodListFragment
    }

    private fun openCreateFoodDialog() {
        val dialog = FoodCreateFragment(this)
        dialog.show(supportFragmentManager, "createFoodDialog")
    }

    override fun onCreatedFood(food: Food) {
        Log.i(
            Constants.DebugLogTag,
            "Main activity communicated new food creation to Adapter. Food: ${food.name}"
        )
        foodListFragment.addFoodToAdapter(food)
    }
}