package com.tfandkusu.observeroom.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore

/**
 * LiveDataでRoomの監視する版
 */
class MainViewModelLiveData(localDataStore: MemberLocalDataStore) : ViewModel() {

    val progress = MutableLiveData<Boolean>()

    init {
        progress.value = true
    }

    val items = Transformations.map(localDataStore.listMembersLiveData()) { src ->
        // プログレス表示を消去
        progress.value = false
        src.mapIndexed { index, item ->
            MemberListItem(
                item.member.id,
                item.member.name,
                item.division.name,
                IMAGE_URLS[index % IMAGE_URLS.size]
            )
        }
    }
}