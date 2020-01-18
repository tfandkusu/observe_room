package com.tfandkusu.observeroom.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.tfandkusu.observeroom.R

class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val itemRoot = itemView.findViewById<ViewGroup>(R.id.item_root)

    val divisionView = itemView.findViewById<TextView>(R.id.division)

    val memberView = itemView.findViewById<TextView>(R.id.member)

}


class MainAdapter(private val listener: Listener) : RecyclerView.Adapter<MainViewHolder>() {

    interface Listener {
        fun onItemClick(item: MemberListItem)
    }

    val items = mutableListOf<MemberListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MainViewHolder(
            inflater.inflate(
                R.layout.main_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.divisionView.text = item.divisionName
        holder.memberView.text = item.memberName
        holder.itemView.setOnClickListener {
            TransitionManager.beginDelayedTransition(holder.itemRoot)
            // listener.onItemClick(item)
            var text = ""
            for (i in 0 until 10) {
                text += item.memberName + " $i\n"
            }
            holder.memberView.text = text
            
        }
    }
}