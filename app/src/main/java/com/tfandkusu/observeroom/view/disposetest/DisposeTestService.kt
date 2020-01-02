package com.tfandkusu.observeroom.view.disposetest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.datastore.Member
import com.tfandkusu.observeroom.datastore.MemberLocalDataStore
import com.tfandkusu.observeroom.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*

/**
 * 2秒待って書き込むを10回
 */
class DisposeTestService : Service() {


    companion object {
        const val ONGOING_NOTIFICATION_ID = 1
    }

    private val dataStore: MemberLocalDataStore by inject()

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d("ObserveRoom", "DisposeTestService#onStart")
        setUpForegroundService()
        val random = Random()
        GlobalScope.launch(Dispatchers.Main) {
            repeat(10) {
                delay(2000)
                val number = random.nextInt(1000)
                dataStore.update(Member(2, "W%03d".format(number), 2))
                Log.d("ObserveRoom", "dao.update $it")
            }
            Log.d("ObserveRoom", "stopForeground")
            stopForeground(true)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     * Foreground Serviceの設定
     */
    private fun setUpForegroundService() {
        // 通知チャンネルID
        val id = getString(R.string.channel_id)
        // 通知チャンネルを作る
        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val notificationChannel = notificationManager.getNotificationChannel(id)
            // 無ければ作る
            if (notificationChannel == null) {
                val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
        }
        // PendingIntentを作る
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.ic_edit_gray_16dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_message))
            .setContentIntent(pendingIntent)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }
}
