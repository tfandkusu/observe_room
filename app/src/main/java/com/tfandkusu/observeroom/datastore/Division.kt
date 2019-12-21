package com.tfandkusu.observeroom.datastore

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 部署
 * @param id
 * @param name 部署名
 */
@Entity
data class Division(@PrimaryKey(autoGenerate = true) val id: Long, val name: String)