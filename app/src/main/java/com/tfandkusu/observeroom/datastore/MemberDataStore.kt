package com.tfandkusu.observeroom.datastore

import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.mooveit.library.Fakeit
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow

interface MemberDataStore {
    /**
     * 初期データ作成
     */
    suspend fun makeFixture()


    /**
     * メンバー更新
     */
    suspend fun update(member: Member)

    suspend fun listDivisions(): List<Division>

    suspend fun get(id: Long): MemberWithDivision?


    /**
     * メンバー一覧を取得(Coroutine Flow版)
     */
    suspend fun listMembersCoroutineFlow(): Flow<List<MemberWithDivision>>

    /**
     * メンバー一覧を取得(RxJava版)
     */
    fun listMembersRxFlowable(): Flowable<List<MemberWithDivision>>

    /**
     * メンバー一覧を取得(LiveData版)
     */
    fun listMembersLiveData(): LiveData<List<MemberWithDivision>>

}


class MemberDataStoreImpl(private val db: MemberDatabase) : MemberDataStore {
    override suspend fun makeFixture() {
        db.withTransaction {
            val dao = db.memberDao()
            if (null == dao.firstMember()) {
                // データが無ければデータを作る
                // 5部署作る
                dao.insert(Division(0, "Sales"))
                dao.insert(Division(0, "Support"))
                dao.insert(Division(0, "Marketing"))
                dao.insert(Division(0, "Development"))
                dao.insert(Division(0, "Management"))
                Fakeit.init()
                // 1部署5人
                dao.listDivisions().map {
                    for (i in 0 until 5) {
                        val name = Fakeit.name().firstName()
                        val member = Member(0, name, it.id)
                        dao.insert(member)
                    }
                }
            }
        }
    }

    override suspend fun listDivisions(): List<Division> {
        return db.memberDao().listDivisions()
    }

    override suspend fun get(id: Long): MemberWithDivision? {
        return db.memberDao().get(id)
    }


    override suspend fun listMembersCoroutineFlow(): Flow<List<MemberWithDivision>> {
        return db.memberDao().listMembersCoroutineFlow()
    }

    override suspend fun update(member: Member) {
        db.memberDao().update(member)
    }

    override fun listMembersRxFlowable(): Flowable<List<MemberWithDivision>> {
        return db.memberDao().listMembersRxFlowable()
    }

    override fun listMembersLiveData(): LiveData<List<MemberWithDivision>> {
        return db.memberDao().listMembersLiveData()
    }


}