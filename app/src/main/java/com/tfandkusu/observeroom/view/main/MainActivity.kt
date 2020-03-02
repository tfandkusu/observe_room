package com.tfandkusu.observeroom.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.view.disposetest.DisposeTestService
import com.tfandkusu.observeroom.view.edit.EditActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    companion object {
        /**
         * スクロール位置
         */
        private const val EXTRA_SCROLL = "scroll"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.progress.observe(this, Observer { flag ->
            flag?.let {
                if (it)
                    progress.visibility = View.VISIBLE
                else
                    progress.visibility = View.GONE
            }
        })
        // RecyclerViewの設定
        val adapter = GroupAdapter<ViewHolder<*>>()
        list.adapter = adapter
//        val adapter = MainAdapter(object : MainAdapter.Listener {
//            override fun onItemClick(item: MemberListItem) {
//                callEditActivity(item)
//            }
//        })
//        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
        list.setHasFixedSize(true)
        viewModel.items.observe(this, Observer { items ->
            adapter.update(items.map {
                MemberGroupieItem(it) {
                    callEditActivity(it)
                }
            })
        })
        // スクロール位置の設定
        viewModel.scroll.observe(this, Observer { index ->
            index?.let {
                Log.d("ObserveRoom", "scroll scrollToPosition $index")
                list.scrollToPosition(it)
            }
        })
        viewModel.onCreate(this, savedInstanceState?.getInt(EXTRA_SCROLL) ?: 0)
    }

    /**
     * 編集画面を開く
     * @param item リスト項目
     */
    private fun callEditActivity(item: MemberListItem) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtras(EditActivity.createCallBundle(item.id))
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            // Roomの購読解除が適切にされているかフォアグランドサービスで確認する
            Log.d("ObserveRoom", "onDestroy")
            val intent = Intent(this, DisposeTestService::class.java)
            startService(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("ObserveRoom", "onSaveInstanceState")
        // スクロール位置の保存
        val firstVisiblePositionItem =
            (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        outState.putInt(EXTRA_SCROLL, firstVisiblePositionItem)
        super.onSaveInstanceState(outState)
    }
}
