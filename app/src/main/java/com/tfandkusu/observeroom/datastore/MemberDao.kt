package com.tfandkusu.observeroom.datastore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM member ORDER BY member.name")
    fun listMembersCoroutineFlow(): Flow<List<MemberWithDivision>>

    @Query("SELECT * FROM member ORDER BY member.name")
    fun listMembersRxFlowable(): Flowable<List<MemberWithDivision>>

    @Query("SELECT * FROM member ORDER BY member.name")
    fun listMembersLiveData(): LiveData<List<MemberWithDivision>>
}