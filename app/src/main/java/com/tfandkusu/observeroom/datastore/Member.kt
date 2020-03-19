package com.tfandkusu.observeroom.datastore

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.tfandkusu.observeroom.data.Division

/**
 * 社員
 * @param id
 * @param name 名前
 * @param divisionId 所属部署ID
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Division::class,
        parentColumns = arrayOf("id"), childColumns = arrayOf("divisionId")
    )]
)
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Long, val name: String,
    val divisionId: Long
)
