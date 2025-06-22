package com.example.gymapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.gymapp.R
import com.example.gymapp.data.DtActivity

/**
 * Interface to handle click events on the edit and delete buttons
 * within each activity list item.
 */
interface OnActivityActionClickListener {
    fun onEditActivity(activity: DtActivity)
    fun onDeleteActivity(activity: DtActivity)
}

/**
 * Custom adapter to display a list of [DtActivity] in a ListView.
 * Includes functionality for edit and delete buttons on each item.
 *
 * @param context The context of the application.
 * @param activities The list of [DtActivity] objects to display.
 * @param listener The listener for the edit/delete click events.
 */
class ActivityListAdapter(
    context: Context,
    private val activities: MutableList<DtActivity>,
    private val listener: OnActivityActionClickListener
) : ArrayAdapter<DtActivity>(context, 0, activities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.item_class_list,
                parent,
                false
            )
        }

        val currentActivity = getItem(position)

        if (currentActivity != null) {
            val tvActivityName: TextView = listItemView!!.findViewById(R.id.tvActivityName)
            val tvActivityPrice: TextView = listItemView.findViewById(R.id.tvActivityPrice)
            val btnEdit: ImageView = listItemView.findViewById(R.id.btnEdit)
            val btnDelete: ImageView = listItemView.findViewById(R.id.btnDelete)

            tvActivityName.text = currentActivity.name
            tvActivityPrice.text = context.getString(R.string.FORMAT_activity_price, currentActivity.price)

            btnEdit.setOnClickListener { listener.onEditActivity(currentActivity) }
            btnDelete.setOnClickListener { listener.onDeleteActivity(currentActivity) }
        }

        return listItemView!!
    }

    fun updateData(newActivities: List<DtActivity>) {
        activities.clear()
        activities.addAll(newActivities)
        notifyDataSetChanged()
    }
}