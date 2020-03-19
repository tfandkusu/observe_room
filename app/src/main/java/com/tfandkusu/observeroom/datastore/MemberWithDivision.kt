package com.tfandkusu.observeroom.datastore

import androidx.room.Embedded
import androidx.room.Relation
import com.tfandkusu.observeroom.data.Division

data class MemberWithDivision(
    @Embedded val member: Member,
    @Relation(
        parentColumn = "divisionId",
        entityColumn = "id"
    )
    val division: Division
)