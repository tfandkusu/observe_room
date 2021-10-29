package com.tfandkusu.observeroom.datastore

import android.content.Context
import androidx.room.Room

object MemberDatabaseFactory {
    fun create(context: Context): MemberDatabase {
        return Room.databaseBuilder(context, MemberDatabase::class.java, "member.sqlite3")
            .build()
    }
}
