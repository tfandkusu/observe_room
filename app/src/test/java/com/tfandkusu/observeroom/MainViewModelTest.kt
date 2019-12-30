package com.tfandkusu.observeroom

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import com.tfandkusu.observeroom.datastore.MemberDataStore
import com.tfandkusu.observeroom.view.main.MainViewModel
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @MockK
    lateinit var dataStore: MemberDataStore

    @MockK
    lateinit var lifecycleOwner: LifecycleOwner


    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)
        viewModel = MainViewModel(dataStore)
    }

    @Test
    fun onCreate() = runBlocking {
        viewModel.progress.value shouldBe true
        viewModel.onCreate(lifecycleOwner, null).join()
    }
}
