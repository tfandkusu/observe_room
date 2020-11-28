package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.datastore.MemberWithDivision
import com.tfandkusu.observeroom.util.LiveDataTestUtil
import com.tfandkusu.observeroom.view.main.MainViewModel
import com.tfandkusu.observeroom.view.main.MemberListItem
import com.tfandkusu.observeroom.view.main.ObserveMode
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MainViewModelTest {

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    /**
     * LiveDataを書き換えるのに必要
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @MockK(relaxed = true)
    lateinit var localDataStore: MemberLocalDataStore

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = MainViewModel(localDataStore)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    /**
     * 初回起動時の処理
     */
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Test
    fun onCreate() = testDispatcher.runBlockingTest {
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
        viewModel.progress.value shouldBe true
        viewModel.onCreate(0).join()
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
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Test
    fun onResultEdit() = testDispatcher.runBlockingTest {
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
                        Member(3L, "edited", 4L),
                        Division(5L, "Management")
                    )
                )
            )
        }
        // 戻ってくる前の状態
        viewModel.progress.value = false
        viewModel.items.value = listOf(
            MemberListItem(1L, "name1", "Sales"),
            MemberListItem(3L, "name2", "Development")
        )
        viewModel.onCreate(0).join()
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
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Test
    fun onCreateRestart() = testDispatcher.runBlockingTest {
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
        viewModel.progress.value shouldBe true
        viewModel.onCreate(1).join()
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
    @ExperimentalCoroutinesApi
    @Test
    fun onCreateRxJava() = testDispatcher.runBlockingTest {
        every {
            localDataStore.listMembersRxFlowable()
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
        viewModel.onCreate(0).join()
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
     * asLiveData版のテスト
     */
    @Test
    fun onCreateAsLiveData() {
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
        // 作成時に上記モックメソッドが実行されるので、ここでViewModelを作る必要がある。
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
