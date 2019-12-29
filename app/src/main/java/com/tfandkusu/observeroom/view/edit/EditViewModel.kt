package com.tfandkusu.observeroom.view.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import com.tfandkusu.observeroom.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject


data class DivisionsAndSelectedId(val divisions: List<Division>, val selectedId: Long)

class EditViewModel(handle: SavedStateHandle) : ViewModel(), KoinComponent {

    private val db: MemberDatabase by inject()

    /**
     * プログレス
     */
    val progress = MutableLiveData<Boolean>()

    /**
     * 部署一覧
     */
    val divisions = MutableLiveData<DivisionsAndSelectedId>()

    /**
     * 選択された部署ID
     */
    val selectedDivisionId = handle.getLiveData<Long>("selectedDivisionId")

    /**
     * メンバー名
     */
    val name = handle.getLiveData<String>("inputName")

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
        val dao = db.memberDao()
        val divisionsList = dao.listDivisions()
        val member = dao.get(id)
        member?.let {
            if (selectedDivisionId.value == null) {
                // 初回ケース
                divisions.value = DivisionsAndSelectedId(divisionsList, it.division.id)
                selectedDivisionId.value = it.division.id
            } else {
                // 画面復帰ケース
                divisions.value =
                    DivisionsAndSelectedId(divisionsList, selectedDivisionId.value ?: 0)
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
        val dao = db.memberDao()
        val member = Member(id, name, divisionId)
        dao.update(member)
        success.value = true
    }

}