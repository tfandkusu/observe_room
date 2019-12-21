package com.tfandkusu.observeroom

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import com.mooveit.library.Fakeit
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import com.tfandkusu.observeroom.datastore.MemberDatabaseFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val items = MutableLiveData<List<MemberListItem>>()

    val progress = MutableLiveData<Boolean>()

    init {
        progress.value = true
    }

    lateinit var db: MemberDatabase

    fun onCreate(context: Context) = GlobalScope.launch(Dispatchers.Main) {
        // 初期データを加える
        db = MemberDatabaseFactory.get(context)
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

    }


}