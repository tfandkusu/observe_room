package com.tfandkusu.observeroom.view.main

import android.util.Log
import android.util.LongSparseArray
import androidx.core.util.set
import androidx.lifecycle.*
import androidx.room.withTransaction
import com.mooveit.library.Fakeit
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import com.tfandkusu.observeroom.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel(handle: SavedStateHandle) : ViewModel(), KoinComponent {

    private val db: MemberDatabase by inject()

    val items = MutableLiveData<List<MemberListItem>>()

    val progress = MutableLiveData<Boolean>()

    var disposable: Disposable? = null


    /**
     * 保存されたスクロール位置
     */
    val saveScroll = handle.getLiveData<Int>("scroll")

    /**
     * スクロール位置制御
     */
    val scroll = SingleLiveEvent<Int>()

    init {
        progress.value = true
    }

    fun onCreate(lifecycleOwner: LifecycleOwner) = viewModelScope.launch(Dispatchers.Main) {
        // 初期データを書き込む
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
            // Coroutine Flowで監視 + スクロール位置を更新された項目の位置にする
            val flow = db.memberDao().listMembersCoroutineFlow()
            flow.collect {
                val oldList = items.value ?: listOf()
                val newList = it.map { src ->
                    MemberListItem(
                        src.member.id,
                        src.member.name,
                        src.division.name
                    )
                }
                items.value = newList
                // 読み込み完了
                progress.value = false
                // スクロール位置復帰
                saveScroll.value?.let { index ->
                    scroll.value = index
                    Log.d("ObserveRoom", "scroll.value = $index")
                    saveScroll.value = null
                }
                // 編集が行われた時はその場所にスクロールする
                updateScroll(oldList, newList)
                Log.d("ObserveRoom", "flow.collect")
            }
            // ここは実行されない
        } else if (false) {
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
                // スクロール
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

    /**
     * スクロール位置を更新された場所にする
     */
    private fun updateScroll(oldList: List<MemberListItem>, newList: List<MemberListItem>) {
        if (oldList.isEmpty())
            return
        // idのマップにする
        val map = LongSparseArray<MemberListItem>()
        oldList.map { map[it.id] = it }
        // 更新された項目を探す
        var scrollIndex = -1
        newList.mapIndexed { index, item ->
            val oldItem = map.get(item.id)
            if (oldItem != item) {
                // 古いものと違う場合は、その場所にスクロールする
                scrollIndex = index
            }
        }
        Log.d("ObserveRoom", "scroll.value = $scrollIndex")
        if (scrollIndex >= 0)
            scroll.value = scrollIndex
    }
}