package com.example.kita_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val childEventsList: List<ChildEvents>,
    private val onEventClick: (Event, String) -> Unit) :
RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CHILD_HEADER = 0
    private val VIEW_TYPE_EVENT = 1

    override fun getItemViewType(position: Int): Int {
        return if (isChildHeader(position)) VIEW_TYPE_CHILD_HEADER else VIEW_TYPE_EVENT
    }

    private fun isChildHeader(position: Int): Boolean {
        var currentPos = 0
        childEventsList.forEach { childEvents ->
            if (currentPos == position) return true
            currentPos += childEvents.events.size + 1
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CHILD_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_child_header, parent, false)
            ChildHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
            EventViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChildHeaderViewHolder) {
            val child = getChildForPosition(position)
            child?.let { holder.bind(it) }
        } else if (holder is EventViewHolder) {
            val child = getChildForPosition(position)
            val event = getEventForPosition(position)

            if (event != null && child != null) {
                holder.bind(event)

                // Pass both the event and childId to the click listener
                holder.itemView.setOnClickListener {
                    onEventClick(event, child.child_id) // Ensure the childId is passed here
                }
            }
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        childEventsList.forEach { childEvents ->
            count += 1 + childEvents.events.size  // 1 for child header, others for events
        }
        return count
    }

    // Get the ChildEvents object for a given position
    private fun getChildForPosition(position: Int): ChildEvents? {
        var pos = position
        for (childEvent in childEventsList) {
            if (pos == 0) return childEvent
            if (pos <= childEvent.events.size) return childEvent
            pos -= (childEvent.events.size + 1)
        }
        return null
    }

    // Get the Event for a given position
    private fun getEventForPosition(position: Int): Event? {
        var pos = position
        for (childEvent in childEventsList) {
            if (pos == 0) return null // Skip header
            pos--
            if (pos < childEvent.events.size) return childEvent.events[pos]
            pos -= childEvent.events.size
        }
        return null
    }

    // ViewHolder for child header (child name, group name)
    class ChildHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(childEvents: ChildEvents?) {
            itemView.findViewById<TextView>(R.id.child_name).text = childEvents?.child_name
            itemView.findViewById<TextView>(R.id.classroom).text = childEvents?.classroom
        }
    }

    // ViewHolder for events
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(event: Event) {
            itemView.findViewById<TextView>(R.id.event_type).text = event.event_type
            itemView.findViewById<TextView>(R.id.event_date).text = event.date
        }
    }
}
