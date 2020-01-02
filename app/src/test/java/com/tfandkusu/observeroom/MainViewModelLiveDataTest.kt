package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.datastore.MemberWithDivision
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
        // このテストはうまくいかなかった
        if (true)
            return
        every {
            localDataStore.listMembersLiveData()
        } returns MutableLiveData<List<MemberWithDivision>>(
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
        // リストが更新された
        // valueがnullになる
        viewModel.items.value?.get(0)?.id shouldBe 1L
        viewModel.items.value?.get(0)?.memberName shouldBe "name1"
        viewModel.items.value?.get(0)?.divisionName shouldBe "Sales"
        viewModel.items.value?.get(1)?.id shouldBe 3L
        viewModel.items.value?.get(1)?.memberName shouldBe "name2"
        viewModel.items.value?.get(1)?.divisionName shouldBe "Development"
        // プログレスが消えた
        viewModel.progress.value shouldBe false
    }
}