package mateusz.oleksik.remindeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mateusz.oleksik.remindeme.Affirmation
import mateusz.oleksik.remindeme.R
import mateusz.oleksik.remindeme.User

class ItemAdapter(
    private val context: Context,
    private val affirmationList: MutableList<Affirmation>,
    private val usersList: List<User>
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_title)
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val userDataTextView : TextView = view.findViewById(R.id.user_data_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.affirmation_item, parent, false)


        return ItemViewHolder(adapterLayout)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val affirmationItem = affirmationList[position]

        val usersIds = listOf<Int>(1, 2, 5, 7, 12)
        val randomUserId = usersIds.asSequence().shuffled().find { true }
        val user = usersList.find { x -> x.uid == randomUserId }

        holder.userDataTextView.text = "${user?.firstName} ${user?.lastName} ID: ${user?.uid}"
        holder.imageView.setImageResource(affirmationItem.imageResourceId)
        holder.textView.text = context.resources.getString(affirmationItem.stringResourceId)

        holder.deleteButton.setOnClickListener(){
            affirmationList.remove(affirmationItem)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return affirmationList.size
    }

}