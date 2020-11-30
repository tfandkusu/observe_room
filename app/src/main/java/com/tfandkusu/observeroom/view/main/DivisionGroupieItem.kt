package com.tfandkusu.observeroom.view.main

import android.view.View
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.databinding.ListItemDivisionBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class DivisionGroupieItem(private val division: String) :
    BindableItem<ListItemDivisionBinding>() {
    override fun bind(viewBinding: ListItemDivisionBinding, position: Int) {
        viewBinding.itemView.tag = "header $division"
        viewBinding.division.text = division
    }

    override fun getLayout() = R.layout.list_item_division

    override fun initializeViewBinding(view: View): ListItemDivisionBinding {
        return ListItemDivisionBinding.bind(view)
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return if (other is DivisionGroupieItem) {
            division == other.division
        } else {
            false
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        return if (other is DivisionGroupieItem) {
            division == other.division
        } else {
            false
        }
    }
}
