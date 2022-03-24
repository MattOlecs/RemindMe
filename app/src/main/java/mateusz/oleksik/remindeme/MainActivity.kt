package mateusz.oleksik.remindeme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mateusz.oleksik.remindeme.fragments.FoodCreateFragment
import mateusz.oleksik.remindeme.fragments.IFoodListFragment
import mateusz.oleksik.remindeme.interfaces.IFoodCreateDialogListener

class MainActivity() : AppCompatActivity(), IFoodCreateDialogListener {

    private lateinit var foodListFragment: IFoodListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            val fragmentTag = "foodList"
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, IFoodListFragment.newInstance(), fragmentTag)
                .commit()
            supportFragmentManager.executePendingTransactions()

            foodListFragment = supportFragmentManager.findFragmentByTag(fragmentTag) as IFoodListFragment

            val addButton: FloatingActionButton = findViewById(R.id.add_food_action_button)
            addButton.setOnClickListener{
                val dialog = FoodCreateFragment(this)
                dialog.show(supportFragmentManager, "createFoodDialog")
            }
        }
    }

    override fun onCreatedFood(food: Food) {
        Log.i("INFO", "On created food called")
        foodListFragment.addFoodToAdapter(food)
    }
}