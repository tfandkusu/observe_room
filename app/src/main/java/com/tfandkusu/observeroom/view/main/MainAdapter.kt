package com.tfandkusu.observeroom.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tfandkusu.observeroom.R

class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val divisionView = itemView.findViewById<TextView>(R.id.division)

    val memberView = itemView.findViewById<TextView>(R.id.member)

}


class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {

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
    }
}