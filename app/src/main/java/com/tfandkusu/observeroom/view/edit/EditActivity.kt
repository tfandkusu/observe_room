package com.tfandkusu.observeroom.view.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tfandkusu.observeroom.R
import kotlinx.android.synthetic.main.activity_edit.*
import org.koin.android.viewmodel.ext.android.viewModel

class EditActivity : AppCompatActivity() {

    companion object {

        private const val EXTRA_ID = "id"

        fun createCallBundle(id: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)
            return bundle
        }
    }

    private val viewModel by viewModel(EditViewModel::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        viewModel.progress.observe(this, Observer { flag ->
            flag?.let {
                if (it) {
                    progress.visibility = View.VISIBLE
                    body.visibility = View.GONE
                } else {
                    progress.visibility = View.GONE
                    body.visibility = View.VISIBLE
                }
            }
        })
        // Spinnerに設定する
        viewModel.divisions.observe(this, Observer { data ->
            data?.let {
                val items = data.divisions.map { it.name }
                val adapter = object : ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    items
                ) {
                    override fun getItemId(position: Int): Long {
                        // idをとれるようにする
                        return data.divisions[position].id
                    }
                }
                division.adapter = adapter
                division.setSelection(it.divisions.indexOfFirst { division -> division.id == it.selectedId })
            }
        })
        // 入力欄
        viewModel.name.observe(this, Observer { data ->
            data?.let {
                name.setText(it)
            }
        })
        // 終了
        viewModel.success.observe(this, Observer {
            finish()
        })
        val id = intent.getLongExtra(EXTRA_ID, 0L)
        viewModel.onCreate(id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            val id = intent.getLongExtra(EXTRA_ID, 0L)
            viewModel.save(id, name.text.toString(), division.selectedItemId)
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 状態保存
        viewModel.selectedDivisionId = division.selectedItemId
        viewModel.inputName = name.text.toString()
    }
}
