package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.datastore.MemberWithDivision
import com.tfandkusu.observeroom.util.LiveDataTestUtil
import com.tfandkusu.observeroom.view.main.MainViewModel
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelAsLiveDataTest {

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    /**
     * LiveDataを書き換えるのに必要
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var localDataStore: MemberLocalDataStore

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    /**
     * asLiveData版のテスト
     */
    @Test
    fun asLiveData() {
        every {
            localDataStore.listMembersCoroutineFlow()
        } returns flow {
            emit(
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
        }
        // テスト対象を作る
        val viewModel = MainViewModel(localDataStore)
        // LiveDataから値を取得する
        val items = LiveDataTestUtil.getValue(viewModel.itemsAsLiveData)
        // 値を確認する
        items[0].id shouldBe 1L
        items[0].memberName shouldBe "name1"
        items[0].divisionName shouldBe "Sales"
        items[1].id shouldBe 3L
        items[1].memberName shouldBe "name2"
        items[1].divisionName shouldBe "Development"
    }
}
