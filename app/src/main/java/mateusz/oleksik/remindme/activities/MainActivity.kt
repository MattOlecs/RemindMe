package mateusz.oleksik.remindme.activities

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
            generateFoodListView()

            _binding.addFoodActionButton.setOnClickListener {
                openCreateFoodDialog()
            }
        }
    }

    private fun generateFoodListView() {
        val foodListFragmentTag = "foodList"
        supportFragmentManager
            .beginTransaction()
            .add(R.id.root_layout, FoodListFragment.newInstance(), foodListFragmentTag)
            .commit()
        supportFragmentManager.executePendingTransactions()

        _foodListFragment =
            supportFragmentManager.findFragmentByTag(foodListFragmentTag) as FoodListFragment
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId != R.id.settings_button){
            return false
        }

        _binding.addFoodActionButton.hide()

        val foodListFragmentTag = "settingsFragment"
        val fragment = supportFragmentManager.findFragmentByTag(foodListFragmentTag)
        if (fragment != null){
            return false
        }

        supportFragmentManager
            .beginTransaction()
            .replace(_foodListFragment.id, SettingsFragment(), foodListFragmentTag)
            .addToBackStack("main")
            .commit()
        supportFragmentManager.executePendingTransactions()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        _binding.addFoodActionButton.show()
    }
}