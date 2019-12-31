package com.tfandkusu.observeroom.view.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent


data class DivisionsAndSelectedId(val divisions: List<Division>, val selectedId: Long)

class EditViewModel(private val localDataStore: MemberLocalDataStore) : ViewModel(), KoinComponent {


    /**
     * プログレス
     */
    val progress = MutableLiveData<Boolean>()

    /**
     * 部署一覧
     */
    val divisions = MutableLiveData<DivisionsAndSelectedId>()

    /**
     * 選択された部署ID(savedInstanceStateにする)
     */
    var selectedDivisionId = 0L

    /**
     * メンバー名(savedInstanceStateに保存する)
     */
    val name = MutableLiveData<String>()

    /**
     * 終了フラグ
     */
    val success = SingleLiveEvent<Boolean>()

    init {
        progress.value = true
    }

    /**
     * ActivityのonCreateから呼ぶ
     * @param id
     */
    fun onCreate(id: Long) = GlobalScope.launch(Dispatchers.Main) {
        val divisionsList = localDataStore.listDivisions()
        val member = localDataStore.get(id)
        member?.let {
            if (selectedDivisionId == 0L) {
                // 初回ケース
                divisions.value = DivisionsAndSelectedId(divisionsList, it.division.id)
                selectedDivisionId = it.division.id
            } else {
                // 画面復帰ケース
                divisions.value =
                    DivisionsAndSelectedId(divisionsList, selectedDivisionId)
            }
            if (name.value == null) {
                // 初回ケース
                name.value = it.member.name
            }
            progress.value = false
        }
    }

    /**
     * 編集内容を保存する
     */
    fun save(id: Long, name: String, divisionId: Long) = GlobalScope.launch(Dispatchers.Main) {
        progress.value = true
        val member = Member(id, name, divisionId)
        localDataStore.update(member)
        success.value = true
    }

}