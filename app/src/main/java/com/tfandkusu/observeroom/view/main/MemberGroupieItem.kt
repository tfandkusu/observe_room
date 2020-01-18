package com.tfandkusu.observeroom.view.main

import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.databinding.MemberGroupieItemBinding
import com.xwray.groupie.databinding.BindableItem

class MemberGroupieItem(private val item: MemberListItem) :
    BindableItem<MemberGroupieItemBinding>(item.id) {

    override fun getLayout(): Int = R.layout.member_groupie_item

    override fun bind(viewBinding: MemberGroupieItemBinding, position: Int) {
        viewBinding.division.text = item.divisionName
        viewBinding.member.text = item.memberName
    }

}