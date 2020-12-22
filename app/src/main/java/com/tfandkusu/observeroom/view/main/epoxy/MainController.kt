package com.tfandkusu.observeroom.view.main.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.tfandkusu.observeroom.view.main.MemberListItem

class MainController(private val onItemClick: (MemberListItem) -> Unit) :
    TypedEpoxyController<List<MemberListItem>>() {

    override
    fun buildModels(items: List<MemberListItem>) {
        items.forEach {
            member {
                id(it.id)
                item(it)
                onClick {
                    onItemClick(it)
                }
            }
        }
    }
}
