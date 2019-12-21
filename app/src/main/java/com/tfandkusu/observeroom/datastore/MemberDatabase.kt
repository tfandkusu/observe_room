package com.tfandkusu.observeroom.datastore

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Member::class, Division::class], version = 1)
abstract class MemberDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
}
