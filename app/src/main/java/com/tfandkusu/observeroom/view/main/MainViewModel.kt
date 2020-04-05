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


/**
 * 画像URL一覧(いらすとやさん)
 */
val IMAGE_URLS = listOf(
    "https://1.bp.blogspot.com/-wBekCTt7lFk/WDASNNQVR8I/AAAAAAAA_6Q/xBlpUKQaSfUEGV7QF2Qcf4lM3PdAYkoNACLcB/s180-c/syukatsu_jiko_appeal_man.png",
    "https://3.bp.blogspot.com/-f7uCoB2z8w4/WBsAHD2t2vI/AAAAAAAA_SY/KAd2OTMCbZ86Kpt_8tCVG2SG9NQBdzlqgCLcB/s180-c/businesswoman5_ureshii.png",
    "https://4.bp.blogspot.com/-w28GEx3_N-c/WASJRBDHo1I/AAAAAAAA_B0/DK820EOiWcwHx4qh0rvDtJ2J5wcIgD38gCLcB/s180-c/pose_douzo_annai_businesswoman.png",
    "https://2.bp.blogspot.com/-LoPwcsgNHpY/WASJRIyrEtI/AAAAAAAA_B4/nYyIB1PE_9Uu9RP2tcv4Hdd-FCkM34RRgCLcB/s180-c/pose_douzo_annai_businessman.png",
    "https://2.bp.blogspot.com/-kre7-857pXQ/Vvpd_CxppUI/AAAAAAAA5TI/3Y__uyCl1uoFOllDQxcjoHCfcBvkErA3Q/s180-c/ue_mezasu_woman.png",
    "https://2.bp.blogspot.com/-pxJY0w5D5yA/Vvpd-IX0BMI/AAAAAAAA5TE/ShzBsF_c8AYmxwVNmcdJwlt4_6y301yAQ/s180-c/ue_mezasu_man.png",
    "https://2.bp.blogspot.com/-imuC9F047vg/VnE4E2F1pgI/AAAAAAAA18o/q1RtMirslQg/s180-c/pose_genki10_businesswoman.png",
    "https://4.bp.blogspot.com/-e-niajZaCVA/VnE4Ddpd0NI/AAAAAAAA18g/ENxtJx7Ej6I/s180-c/pose_genki09_businessman.png",
    "https://2.bp.blogspot.com/-frz2gd8ClD0/VnE3TZBcPSI/AAAAAAAA10Y/3dbKXH2OMPU/s180-c/pose_makasenasai_woman.png",
    "https://2.bp.blogspot.com/-1mLjLjlWnMk/ViipNaBhgFI/AAAAAAAAzys/k-1Gdd3zMWA/s180-c/business_suit_good_woman.png",
    "https://4.bp.blogspot.com/-02hTJqtLKic/VhB9jqbjsVI/AAAAAAAAyzo/eY_8lITc36Q/s180-c/businessman_dekiru_woman.png",
    "https://1.bp.blogspot.com/-rBFzjQbEFj4/VhB9jvnHAmI/AAAAAAAAyzs/R1Dwa7c5l78/s180-c/businessman_dekiru.png",
    "https://4.bp.blogspot.com/-2SF2PLLFwi4/VfS6hXoS6yI/AAAAAAAAxRc/MhwXwHHQAW8/s180-c/mokuhyou_tassei_woman.png"
)


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
                                list.mapIndexed { index, item ->
                                    MemberListItem(
                                        item.member.id,
                                        item.member.name,
                                        item.division.name,
                                        IMAGE_URLS[index % IMAGE_URLS.size]
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
                            val flow = localDataStore.listMembersCoroutineFlow().map { list ->
                                list.mapIndexed { index, item ->
                                    MemberListItem(
                                        item.member.id,
                                        item.member.name,
                                        item.division.name,
                                        IMAGE_URLS[index % IMAGE_URLS.size]
                                    )
                                }
                            }
                            flow.collect {
                                items.value = it
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
                            items.value = it.mapIndexed { index, item ->
                                MemberListItem(
                                    item.member.id,
                                    item.member.name,
                                    item.division.name,
                                    IMAGE_URLS[index % IMAGE_URLS.size]
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
                            items.value = it.mapIndexed { index, item ->
                                MemberListItem(
                                    item.member.id,
                                    item.member.name,
                                    item.division.name,
                                    IMAGE_URLS[index % IMAGE_URLS.size]
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