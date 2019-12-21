package com.tfandkusu.observeroom.datastore

import android.content.Context
import androidx.room.Room


object MemberDatabaseFactory {

    var db : MemberDatabase? = null

    fun get(context: Context): MemberDatabase {
        if(db == null)
            db = Room.databaseBuilder(context, MemberDatabase::class.java, "member.sqlite3").build()
        return db as MemberDatabase
    }
}