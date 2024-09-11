package com.example.kita_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val childEventsList: List<ChildEvents>,
    private val onEventClick: (Event) -> Unit) :
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
            holder.bind(child)
        } else if (holder is EventViewHolder) {
            val event = getEventForPosition(position)
            event?.let {
                holder.bind(it)  // Only bind if event is not null
                holder.itemView.setOnClickListener { onEventClick(event) }
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

    private fun getChildForPosition(position: Int): ChildEvents? {
        var currentPos = 0
        childEventsList.forEach { childEvents ->
            if (currentPos == position) return childEvents
            currentPos += childEvents.events.size + 1
        }
        return null
    }

    private fun getEventForPosition(position: Int): Event? {
        var currentPos = 0
        childEventsList.forEach { childEvents ->
            currentPos += 1  // Skip the child header
            if (position < currentPos + childEvents.events.size) {
                return childEvents.events[position - currentPos]
            }
            currentPos += childEvents.events.size
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
