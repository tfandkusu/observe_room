package com.tfandkusu.observeroom.view.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tfandkusu.observeroom.datastore.Division
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


data class DivisionsAndSelectedId(val divisions: List<Division>, val selectedId: Long)

class EditViewModel(val db: MemberDatabase) : ViewModel() {

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
    var selectedDivisionId = 0L

    /**
     * 入力されたメンバー名
     */
    var inputName: String? = null

    /**
     * メンバー名
     */
    val name = MutableLiveData<String>()

    /**
     * 終了フラグ
     */
    val success = MutableLiveData<Boolean>()

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
            if (selectedDivisionId >= 1) {
                // 画面復帰ケース
                divisions.value = DivisionsAndSelectedId(divisionsList, selectedDivisionId)
            } else {
                // 初回ケース
                divisions.value = DivisionsAndSelectedId(divisionsList, it.division.id)
                selectedDivisionId = it.division.id
            }
            if (inputName != null) {
                // 画面復帰ケース
                name.value = inputName
            } else {
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