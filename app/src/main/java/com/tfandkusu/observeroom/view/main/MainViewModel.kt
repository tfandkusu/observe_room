package com.tfandkusu.observeroom.view.main

import android.util.Log
import android.util.LongSparseArray
import androidx.core.util.set
import androidx.lifecycle.*
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.util.SingleLiveEvent
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 更新監視の方法
 */
enum class ObserveMode {
    /**
     * Coroutine Flowで監視
     */
    COROUTINE_FLOW,
    /**
     * RxJavaで監視
     */
    RX_JAVA,
    /**
     * LiveDataで監視
     */
    LIVE_DATA
}

class MainViewModel(private val localDataStore: MemberLocalDataStore) : ViewModel() {

    var mode = ObserveMode.COROUTINE_FLOW

    /**
     * 保存されたスクロール位置
     */
    var savedScroll = 0

    val items = MutableLiveData<List<MemberListItem>>()

    val progress = MutableLiveData<Boolean>()

    var disposable: Disposable? = null

    var firstTime = true

    /**
     * スクロール位置制御
     */
    val scroll = SingleLiveEvent<Int>()

    init {
        progress.value = true
    }

    fun onCreate(lifecycleOwner: LifecycleOwner, savedScroll: Int) =
        viewModelScope.launch(Dispatchers.Main) {
            // 初期データを書き込む
            localDataStore.makeFixture()
            // リスト表示用のデータを取得する
            when (mode) {
                ObserveMode.COROUTINE_FLOW -> {
                    if (true) {
                        this@MainViewModel.savedScroll = savedScroll
                        if (firstTime) {
                            // 最初の1回だけ実行する
                            firstTime = false
                            // Coroutine Flowで監視
                            val flow = localDataStore.listMembersCoroutineFlow().map { list ->
                                list.map {
                                    MemberListItem(
                                        it.member.id,
                                        it.member.name,
                                        it.division.name
                                    )
                                }
                            }
                            flow.collect {
                                val oldList = items.value ?: listOf()
                                val newList = it
                                items.value = newList
                                // 読み込み完了
                                progress.value = false
                                // 編集が行われた時はその場所にスクロールする
                                updateScroll(oldList, newList)
                                Log.d("ObserveRoom", "flow.collect")
                            }
                        }
                        // ここは実行されない
                    } else {
                        // Coroutine Flowで監視
                        if (firstTime) {
                            // 最初の1回だけ実行する
                            firstTime = false
                            val flow = localDataStore.listMembersCoroutineFlow()
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
                    }
                }
                ObserveMode.RX_JAVA -> {
                    // RxJavaのFlowableで監視
                    if (disposable == null) {
                        val flowable = localDataStore.listMembersRxFlowable()
                        disposable = flowable.subscribe({
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
                        }, {
                            it.printStackTrace()
                        })
                    }
                }
                ObserveMode.LIVE_DATA -> {
                    // LiveDataで監視
                    val liveData = localDataStore.listMembersLiveData()
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
        if (savedScroll >= 1) {
            // 回転またはプロセスキルからの復帰ケース
            scroll.value = savedScroll
            savedScroll = 0
            return
        }
        if (oldList.isEmpty()) {
            // 初回ケース
            return
        }
        // 更新ケース
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
        if (scrollIndex >= 0) {
            scroll.value = scrollIndex
        }
    }
}