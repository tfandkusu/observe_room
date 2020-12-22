package com.tfandkusu.observeroom.view.main

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import io.doist.recyclerviewext.sticky_headers.StickyHeaders

class StickyHeaderGroupAdapter : GroupAdapter<GroupieViewHolder>(), StickyHeaders {
    override fun isStickyHeader(position: Int): Boolean {
        return getItem(position) is DivisionGroupieItem
    }
}
