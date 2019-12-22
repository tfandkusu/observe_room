package com.tfandkusu.observeroom.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfandkusu.observeroom.R
import com.tfandkusu.observeroom.view.edit.EditActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel(MainViewModel::class)

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

        viewModel.onCreate()
    }

    private fun callEditActivity(item: MemberListItem) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtras(EditActivity.createCallBundle(item.id))
        startActivity(intent)
    }
}
