package mateusz.oleksik.remindeme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mateusz.oleksik.remindeme.fragments.FoodCreateFragment
import mateusz.oleksik.remindeme.fragments.FoodListFragment
import mateusz.oleksik.remindeme.interfaces.FoodCreateDialogListener
import java.time.LocalDate
import java.util.*

class MainActivity() : AppCompatActivity(), FoodCreateDialogListener {

    private lateinit var foodListFragment: FoodListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            var fragmentTag: String = "foodList"
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, FoodListFragment.newInstance(), fragmentTag)
                .commit()
            supportFragmentManager.executePendingTransactions()

            foodListFragment = supportFragmentManager.findFragmentByTag(fragmentTag) as FoodListFragment

            if (foodListFragment != null){

                val addButton: FloatingActionButton = findViewById(R.id.add_food_action_button)

                addButton.setOnClickListener{
                    var dialog = FoodCreateFragment(this)
                    dialog.show(supportFragmentManager, "createFoodDialog")
                    //foodListFragment.addFoodToAdapter(Food(0, "Agrest", 20220101))
                }
            }
        }
    }

    override fun onCreatedFood(food: Food) {
        Log.i("APPINFO", "On created food called")
        foodListFragment.addFoodToAdapter(food)
    }
}