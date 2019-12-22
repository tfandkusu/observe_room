package com.tfandkusu.observeroom.datastore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MemberDao {
    @Insert
    suspend fun insert(division: Division)

    @Insert
    suspend fun insert(member: Member)

    @Query("SELECT * FROM member ORDER BY id LIMIT 1")
    suspend fun firstMember(): Member?

    @Query("SELECT * FROM division ORDER BY id")
    suspend fun listDivisions(): List<Division>

    @Query("SELECT * FROM member ORDER BY member.name")
    suspend fun listMembers(): List<MemberWithDivision>

    @Query("SELECT * FROM member WHERE id=:id")
    suspend fun get(id: Long): MemberWithDivision?

    @Update
    suspend fun update(member: Member)
}