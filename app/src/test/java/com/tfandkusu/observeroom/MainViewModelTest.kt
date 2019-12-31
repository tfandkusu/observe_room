package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDataStore
import com.tfandkusu.observeroom.datastore.MemberWithDivision
import com.tfandkusu.observeroom.view.main.MainViewModel
import com.tfandkusu.observeroom.view.main.MemberListItem
import com.tfandkusu.observeroom.view.main.ObserveMode
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @MockK(relaxed = true)
    lateinit var dataStore: MemberDataStore

    @MockK(relaxed = true)
    lateinit var lifecycleOwner: LifecycleOwner


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)
        viewModel = MainViewModel(dataStore)
    }

    /**
     * 初回起動時の処理
     */
    @InternalCoroutinesApi
    @Test
    fun onCreate() = runBlocking {
        // もっとよい書き方がありそう
        coEvery {
            dataStore.listMembersCoroutineFlow()
        } returns testFlow(
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
        viewModel.progress.value shouldBe true
        viewModel.onCreate(lifecycleOwner, null).join()
        // リストが更新された
        viewModel.items.value?.get(0)?.id shouldBe 1L
        viewModel.items.value?.get(0)?.memberName shouldBe "name1"
        viewModel.items.value?.get(0)?.divisionName shouldBe "Sales"
        viewModel.items.value?.get(1)?.id shouldBe 3L
        viewModel.items.value?.get(1)?.memberName shouldBe "name2"
        viewModel.items.value?.get(1)?.divisionName shouldBe "Development"
        // プログレスが消えた
        viewModel.progress.value shouldBe false
        // スクロールは特に制御しない
        viewModel.scroll.value shouldBe null
    }

    /**
     * 編集画面から戻ってきた時の処理
     */
    @InternalCoroutinesApi
    @Test
    fun onResultEdit() = runBlocking {
        coEvery {
            dataStore.listMembersCoroutineFlow()
        } returns testFlow(
            listOf(
                MemberWithDivision(
                    Member(1L, "name1", 2L),
                    Division(2L, "Sales")
                ),
                MemberWithDivision(
                    Member(3L, "edited", 4L),
                    Division(5L, "Management")
                )
            )
        )
        // 戻ってくる前の状態
        viewModel.progress.value = false
        viewModel.items.value = listOf(
            MemberListItem(1L, "name1", "Sales"),
            MemberListItem(3L, "name2", "Development")
        )
        viewModel.onCreate(lifecycleOwner, null).join()
        // リストが更新された
        viewModel.items.value?.get(0)?.id shouldBe 1L
        viewModel.items.value?.get(0)?.memberName shouldBe "name1"
        viewModel.items.value?.get(0)?.divisionName shouldBe "Sales"
        viewModel.items.value?.get(1)?.id shouldBe 3L
        viewModel.items.value?.get(1)?.memberName shouldBe "edited"
        viewModel.items.value?.get(1)?.divisionName shouldBe "Management"
        // スクロールは制御
        viewModel.scroll.value shouldBe 1
    }

    /**
     * プロセス復帰時の処理
     */
    @InternalCoroutinesApi
    @Test
    fun onCreateRestart() = runBlocking {
        coEvery {
            dataStore.listMembersCoroutineFlow()
        } returns testFlow(
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
        viewModel.progress.value shouldBe true
        viewModel.onCreate(lifecycleOwner, 1).join()
        // リストが更新された
        viewModel.items.value?.get(0)?.id shouldBe 1L
        viewModel.items.value?.get(0)?.memberName shouldBe "name1"
        viewModel.items.value?.get(0)?.divisionName shouldBe "Sales"
        viewModel.items.value?.get(1)?.id shouldBe 3L
        viewModel.items.value?.get(1)?.memberName shouldBe "name2"
        viewModel.items.value?.get(1)?.divisionName shouldBe "Development"
        // プログレスが消えた
        viewModel.progress.value shouldBe false
        // スクロールの制御
        viewModel.scroll.value shouldBe 1
    }


    /**
     * RxJava版のテスト
     */
    @Test
    fun onCreateRxJava() = runBlocking {
        every {
            dataStore.listMembersRxFlowable()
        } returns Flowable.just(
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
        // RxJava版を使う
        viewModel.mode = ObserveMode.RX_JAVA
        viewModel.progress.value shouldBe true
        viewModel.onCreate(lifecycleOwner, null).join()
        // リストが更新された
        viewModel.items.value?.get(0)?.id shouldBe 1L
        viewModel.items.value?.get(0)?.memberName shouldBe "name1"
        viewModel.items.value?.get(0)?.divisionName shouldBe "Sales"
        viewModel.items.value?.get(1)?.id shouldBe 3L
        viewModel.items.value?.get(1)?.memberName shouldBe "name2"
        viewModel.items.value?.get(1)?.divisionName shouldBe "Development"
        // プログレスが消えた
        viewModel.progress.value shouldBe false
        // スクロールは特に制御しない
        viewModel.scroll.value shouldBe null
    }

    /**
     * LiveData版のテスト
     */
    @Test
    fun onCreateLiveData() = runBlocking {
        every {
            dataStore.listMembersLiveData()
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
        viewModel.mode = ObserveMode.LIVE_DATA
        viewModel.progress.value shouldBe true
        viewModel.items.observeForever {
            // リストが更新された
            viewModel.items.value?.get(0)?.id shouldBe 1L
            viewModel.items.value?.get(0)?.memberName shouldBe "name1"
            viewModel.items.value?.get(0)?.divisionName shouldBe "Sales"
            viewModel.items.value?.get(1)?.id shouldBe 3L
            viewModel.items.value?.get(1)?.memberName shouldBe "name2"
            viewModel.items.value?.get(1)?.divisionName shouldBe "Development"
            // プログレスが消えた
            viewModel.progress.value shouldBe false
            // スクロールは特に制御しない
            viewModel.scroll.value shouldBe null
        }
        viewModel.onCreate(lifecycleOwner, null).join()
    }
}
