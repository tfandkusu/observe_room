package com.tfandkusu.observeroom.view.main.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tfandkusu.observeroom.R

@EpoxyModelClass(layout = R.layout.list_item_division)
abstract class DivisionModel : EpoxyModelWithHolder<DivisionModel.Holder>() {
    @EpoxyAttribute
    lateinit var division: String

    override fun bind(holder: Holder) {
        holder.root.tag = "header $division"
        holder.division.text = division
    }

    class Holder : EpoxyHolder() {

        lateinit var root: View

        lateinit var division: TextView

        override fun bindView(itemView: View) {
            root = itemView.rootView
            division = itemView.findViewById(R.id.division)
        }
    }
}
