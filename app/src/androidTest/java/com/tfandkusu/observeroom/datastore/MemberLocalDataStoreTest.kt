package com.tfandkusu.observeroom.datastore

import androidx.test.platform.app.InstrumentationRegistry
import io.kotlintest.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MemberLocalDataStoreTest {

    private lateinit var localDataStore: MemberLocalDataStore

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = MemberDatabaseFactory.create(context)
        localDataStore = MemberLocalDataStoreImpl(db)
    }

    @Test
    fun test() = runBlocking {
        localDataStore.makeFixture()
        // 部署と社員の保存を確認
        localDataStore.listDivisions().map { it.name } shouldBe listOf(
            "Sales", "Support", "Marketing", "Development", "Management"
        )
        val memberWithDivisionList = localDataStore.listMembersCoroutineFlow().first()
        memberWithDivisionList.size shouldBe 25
        // 更新を確認
        val editedMember = memberWithDivisionList[0].member.copy(name = "Edited")
        localDataStore.update(editedMember)
        val memberWithDivision = localDataStore.get(editedMember.id)
        memberWithDivision?.member?.name shouldBe "Edited"
    }
}
