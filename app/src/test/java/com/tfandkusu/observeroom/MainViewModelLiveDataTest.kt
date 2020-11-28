package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.datastore.MemberWithDivision
import com.tfandkusu.observeroom.util.LiveDataTestUtil
import com.tfandkusu.observeroom.view.main.MainViewModelLiveData
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelLiveDataTest {
    /**
     * LiveDataを書き換えるのに必要
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var localDataStore: MemberLocalDataStore

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun test() {
        // LiveDataを返却するモック実装を作る
        every {
            localDataStore.listMembersLiveData()
        } returns MutableLiveData(
            listOf(
                MemberWithDivision(
                    Member(1L, "name1", 2L),
                    Division(2L, "Sales")
                ),
                MemberWithDivision(
                    Member(3L, "name2", 4L),
                    Division(4L, "Development")
                )
            )
        )
        // テスト対象を作る
        val viewModel = MainViewModelLiveData(localDataStore)
        // LiveDataの値を取得する
        val items = LiveDataTestUtil.getValue(viewModel.items)
        val progress = LiveDataTestUtil.getValue(viewModel.progress)
        // 値を確認する
        items[0].id shouldBe 1L
        items[0].memberName shouldBe "name1"
        items[0].divisionName shouldBe "Sales"
        items[1].id shouldBe 3L
        items[1].memberName shouldBe "name2"
        items[1].divisionName shouldBe "Development"
        // プログレスが消えた
        progress shouldBe false
    }
}
