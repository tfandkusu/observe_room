package com.tfandkusu.observeroom

import android.app.Application
import androidx.room.Room
import com.tfandkusu.observeroom.datastore.MemberDatabase
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.datastore.MemberLocalDataStoreImpl
import com.tfandkusu.observeroom.view.edit.EditViewModel
import com.tfandkusu.observeroom.view.main.MainViewModel
import com.tfandkusu.observeroom.view.main.MainViewModelLiveData
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val myModule = module {
            single {
                Room.databaseBuilder(androidContext(), MemberDatabase::class.java, "member.sqlite3")
                    .build()
            }
            single {
                MemberLocalDataStoreImpl(get()) as MemberLocalDataStore
            }
            viewModel {
                MainViewModel(get())
            }
            viewModel {
                MainViewModelLiveData(get())
            }
            viewModel {
                EditViewModel(get())
            }
        }
        startKoin {
            androidContext(this@MyApplication)
            modules(myModule)
        }

    }
}