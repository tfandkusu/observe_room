package com.tfandkusu.observeroom.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * ライフサイクルによらず一度だけ通知するLiveData
 */
class SingleLiveEvent<T> {

    /**
     * LiveDataをラップする
     */
    private val liveData = MutableLiveData<T>()

    /**
     * onChangedを呼んだフラグ
     */
    private var onChangedFlag = false


    /**
     * 値書き込みをラップする
     */
    var value: T?
        set(v) {
            onChangedFlag = false
            liveData.value = v
        }
        get() {
            return liveData.value
        }

    /**
     * 更新監視をラップする
     */
    fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        liveData.observe(owner, Observer<T> { v ->
            if (!onChangedFlag) {
                observer.onChanged(v)
                onChangedFlag = true
            }
        })
    }
}