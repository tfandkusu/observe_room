package com.tfandkusu.observeroom.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.view.disposetest.DisposeTestService
import com.tfandkusu.observeroom.view.edit.EditActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        SavedStateViewModelFactory(application, this)
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
        val adapter = MainAdapter(object : MainAdapter.Listener {
            override fun onItemClick(item: MemberListItem) {
                callEditActivity(item)
            }
        })
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
        list.setHasFixedSize(true)
        viewModel.items.observe(this, Observer { items ->
            items?.let {
                adapter.items.clear()
                adapter.items.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.scroll.observe(this, Observer { index ->
            index?.let {
                Log.d("ObserveRoom", "scroll scrollToPosition $index")
                list.scrollToPosition(it)
            }
        })
        viewModel.onCreate(this)
    }

    private fun callEditActivity(item: MemberListItem) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtras(EditActivity.createCallBundle(item.id))
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            Log.d("ObserveRoom", "onDestroy")
            val intent = Intent(this, DisposeTestService::class.java)
            startService(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("ObserveRoom", "onSaveInstanceState")
        val firstVisiblePositionItem =
            (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        viewModel.saveScroll.value = firstVisiblePositionItem
        super.onSaveInstanceState(outState)
    }
}
