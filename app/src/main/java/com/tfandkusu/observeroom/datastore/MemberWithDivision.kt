package com.tfandkusu.observeroom.datastore

import androidx.room.Embedded
import androidx.room.Relation

data class MemberWithDivision(
    @Embedded val member: Member,
    @Relation(
        parentColumn = "divisionId",
        entityColumn = "id"
    )
    val division: Division
)