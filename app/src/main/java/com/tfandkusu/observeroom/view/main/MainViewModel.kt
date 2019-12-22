package com.tfandkusu.observeroom.view.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.mooveit.library.Fakeit
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val db: MemberDatabase) : ViewModel() {
    val items = MutableLiveData<List<MemberListItem>>()

    val progress = MutableLiveData<Boolean>()

    init {
        progress.value = true
    }

    fun onCreate() = GlobalScope.launch(Dispatchers.Main) {
        // 初期データを加える
        db.withTransaction {
            val dao = db.memberDao()
            if (null == dao.firstMember()) {
                // データが無ければデータを作る
                // 5部署作る
                dao.insert(Division(0, "Sales"))
                dao.insert(Division(0, "Support"))
                dao.insert(Division(0, "Marketing"))
                dao.insert(Division(0, "Development"))
                dao.insert(Division(0, "Management"))
                Fakeit.init()
                // 1部署5人
                dao.listDivisions().map {
                    for (i in 0 until 5) {
                        val name = Fakeit.name().firstName()
                        val member = Member(0, name, it.id)
                        dao.insert(member)
                    }
                }
            }
        }
        // リスト表示用のデータを取得する
        if (true) {
            // Coroutine Flowで監視
            val flow = db.memberDao().listMembersCoroutineFlow()
            viewModelScope.launch {
                flow.collect {
                    items.value = it.map { src ->
                        MemberListItem(
                            src.member.id,
                            src.member.name,
                            src.division.name
                        )
                    }
                    // 読み込み完了
                    progress.value = false
                    Log.d("ObserveRoom", "flow.collect")
                }
                // ここは実行されない
            }
        } else {
            // 監視なし
            val list = db.memberDao().listMembers()
            items.value = list.map {
                MemberListItem(
                    it.member.id,
                    it.member.name,
                    it.division.name
                )
            }
        }
    }

}