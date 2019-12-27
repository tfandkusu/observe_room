package com.tfandkusu.observeroom.view.main

import android.util.Log
import androidx.lifecycle.*
import androidx.room.withTransaction
import com.mooveit.library.Fakeit
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val db: MemberDatabase) : ViewModel() {
    val items = MutableLiveData<List<MemberListItem>>()

    val progress = MutableLiveData<Boolean>()

    var disposable: Disposable? = null

    init {
        progress.value = true
    }

    fun onCreate(lifecycleOwner: LifecycleOwner) = viewModelScope.launch(Dispatchers.Main) {
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
        } else if (false) {
            // RxJavaのFlowableで監視
            if (disposable == null) {
                val flowable = db.memberDao().listMembersRxFlowable()
                disposable = flowable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        items.value = it.map { src ->
                            MemberListItem(
                                src.member.id,
                                src.member.name,
                                src.division.name
                            )
                        }
                        // 読み込み完了
                        progress.value = false
                        Log.d("ObserveRoom", "flowable.subscribe")
                    }
            }
        } else if (false) {
            // LiveDataで監視
            val liveData = db.memberDao().listMembersLiveData()
            liveData.observe(lifecycleOwner, Observer { src ->
                src?.let {
                    items.value = it.map { srcItem ->
                        MemberListItem(
                            srcItem.member.id,
                            srcItem.member.name,
                            srcItem.division.name
                        )
                    }
                }
                // 読み込み完了
                progress.value = false
                Log.d("ObserveRoom", "LiveData observer")
            })
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

    override fun onCleared() {
        disposable?.dispose()
    }
}