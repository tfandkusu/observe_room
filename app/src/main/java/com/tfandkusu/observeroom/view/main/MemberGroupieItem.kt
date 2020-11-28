package com.tfandkusu.observeroom.view.main

import android.view.View
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.databinding.MemberGroupieItemBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class MemberGroupieItem(private val item: MemberListItem, private val onClick: () -> Unit) :
    BindableItem<MemberGroupieItemBinding>() {

    override fun getLayout(): Int = R.layout.member_groupie_item

    override fun bind(viewBinding: MemberGroupieItemBinding, position: Int) {
        viewBinding.division.text = item.divisionName
        viewBinding.member.text = item.memberName
        viewBinding.root.setOnClickListener {
            onClick()
        }
    }

    override fun initializeViewBinding(view: View): MemberGroupieItemBinding {
        return MemberGroupieItemBinding.bind(view)
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return if (other is MemberGroupieItem) {
            item.id == other.item.id
        } else {
            false
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        return if (other is MemberGroupieItem) {
            item == other.item
        } else {
            false
        }
    }
}
