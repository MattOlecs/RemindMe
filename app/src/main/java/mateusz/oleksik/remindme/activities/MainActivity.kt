package mateusz.oleksik.remindme.acitivties

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import mateusz.oleksik.remindme.R
import mateusz.oleksik.remindme.databinding.ActivityMainBinding
import mateusz.oleksik.remindme.fragments.FoodCreateFragment
import mateusz.oleksik.remindme.fragments.FoodListFragment
import mateusz.oleksik.remindme.fragments.SettingsFragment
import mateusz.oleksik.remindme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindme.models.Food
import mateusz.oleksik.remindme.utils.Constants

class MainActivity : AppCompatActivity(), IFoodCreateDialogListener {

    private lateinit var _foodListFragment: FoodListFragment
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.myToolbar)

        if (savedInstanceState == null) {
            generateFoodList()

            _binding.addFoodActionButton.setOnClickListener {
                openCreateFoodDialog()
            }
        }
    }

    private fun generateFoodList() {
        val fragmentTag = "foodList"
        supportFragmentManager
            .beginTransaction()
            .add(R.id.root_layout, FoodListFragment.newInstance(), fragmentTag)
            .addToBackStack("main")
            .commit()
        supportFragmentManager.executePendingTransactions()

        _foodListFragment =
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
        _foodListFragment.addFoodToAdapter(food)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_action_buttons, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings_button -> {
            supportFragmentManager
                .beginTransaction()
                .replace(_foodListFragment.id, SettingsFragment())
                .addToBackStack("main")
                .commit()
            supportFragmentManager.executePendingTransactions()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}