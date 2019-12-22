package com.tfandkusu.observeroom.view.disposetest

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * 2秒待って書き込むを10回
 */
class DisposeTestService : IntentService("DisposeTestService") {


    private val db: MemberDatabase by inject()


    override fun onHandleIntent(intent: Intent?) {
        Log.d("ObserveRoom", "onHandleIntent")
        GlobalScope.launch(Dispatchers.Main) {
            repeat(10) {
                delay(2000)
                val dao = db.memberDao()
                val number = System.currentTimeMillis() % 1000
                dao.update(Member(2, "W%03d".format(number), 2))
                Log.d("ObserveRoom", "dao.update")
            }
        }
    }
}
