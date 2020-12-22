package com.tfandkusu.observeroom.view.main.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.tfandkusu.observeroom.view.main.MemberListItem

class MainController(private val onItemClick: (MemberListItem) -> Unit) :
    TypedEpoxyController<List<MemberListItem>>() {

    override
    fun buildModels(items: List<MemberListItem>) {
        var lastDivision = ""
        items.forEach {
            if (lastDivision != it.divisionName) {
                division {
                    id(it.divisionName)
                    division(it.divisionName)
                }
                lastDivision = it.divisionName
            }
            member {
                id(it.id)
                item(it)
                onClick {
                    onItemClick(it)
                }
            }
        }
    }

    override fun isStickyHeader(position: Int): Boolean {
        return adapter.getModelAtPosition(position)::class == DivisionModel_::class
    }
}
