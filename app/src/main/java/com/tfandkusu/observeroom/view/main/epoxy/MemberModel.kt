package com.tfandkusu.observeroom.view.main.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.view.main.MemberListItem

@EpoxyModelClass(layout = R.layout.main_list_item)
abstract class MemberModel : EpoxyModelWithHolder<MemberModel.Holder>() {
    @EpoxyAttribute
    lateinit var item: MemberListItem

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClick: () -> Unit

    override fun bind(holder: Holder) {
        holder.root.setOnClickListener {
            onClick()
        }
        holder.division.text = item.divisionName
        holder.member.text = item.memberName
    }

    class Holder : EpoxyHolder() {

        lateinit var root: View

        lateinit var division: TextView

        lateinit var member: TextView

        override fun bindView(itemView: View) {
            root = itemView.rootView
            division = itemView.findViewById(R.id.division)
            member = itemView.findViewById(R.id.member)
        }
    }
}
