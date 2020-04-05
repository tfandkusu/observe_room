package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.datastore.MemberWithDivision
import com.tfandkusu.observeroom.view.edit.EditViewModel
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditViewModelTest {
    /**
     * LiveDataを書き換えるのに必要
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var localDataStore: MemberLocalDataStore

    private lateinit var viewModel: EditViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        // スレッドを切り替えない
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)
        viewModel = EditViewModel(localDataStore)
    }


    /**
     * 初回ケース
     */
    @Test
    fun onCreateInit() = runBlocking {
        coEvery {
            localDataStore.listDivisions()
        } returns listOf(Division(1L, "Sales"), Division(2L, "Support"))
        coEvery {
            localDataStore.get(3L)
        } returns MemberWithDivision(Member(3L, "Name3", 1L), Division(1L, "Sales"))
        viewModel.progress.value shouldBe true
        viewModel.onCreate(3L).join()
        viewModel.divisions.value?.divisions shouldBe listOf(
            Division(1L, "Sales"),
            Division(2L, "Support")
        )
        viewModel.divisions.value?.selectedId shouldBe 1L
        viewModel.selectedDivisionId shouldBe 1L
        viewModel.name.value shouldBe "Name3"
        viewModel.progress.value shouldBe false
    }


    /**
     * 画面回転、プロセス復帰ケース
     */
    @Test
    fun onCreateRestore() = runBlocking {
        // SavedInstanceStateから復帰
        viewModel.selectedDivisionId = 2L
        viewModel.name.value = "Edited"
        coEvery {
            localDataStore.listDivisions()
        } returns listOf(Division(1L, "Sales"), Division(2L, "Support"))
        coEvery {
            localDataStore.get(3L)
        } returns MemberWithDivision(Member(3L, "Name3", 1L), Division(1L, "Sales"))
        viewModel.progress.value shouldBe true
        viewModel.onCreate(3L).join()
        viewModel.divisions.value?.divisions shouldBe listOf(
            Division(1L, "Sales"),
            Division(2L, "Support")
        )
        viewModel.divisions.value?.selectedId shouldBe 2L
        viewModel.selectedDivisionId shouldBe 2L
        viewModel.name.value shouldBe "Edited"
        viewModel.progress.value shouldBe false
    }

    /**
     * 保存する
     */
    @Test
    fun save() = runBlocking {
        viewModel.progress.value = false
        viewModel.save(1L, "Edited", 2L).join()
        coVerify(exactly = 1) {
            localDataStore.update(Member(1L, "Edited", 2L))
        }
        viewModel.progress.value shouldBe true
        viewModel.success.value shouldBe true
    }

}