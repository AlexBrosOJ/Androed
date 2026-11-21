package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.AtomicFile
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val tasks = arrayListOf<String>()

    private lateinit var tasksFile: AtomicFile

    private fun loadTasks() {
        if (!tasksFile.baseFile.exists()) return
        val json = JSONArray(tasksFile.readFully().toString(Charsets.UTF_8))
        tasks.addAll(List(json.length()) { json.getString(it) })
    }

    private fun showLoadError() {
        Toast.makeText(this, "Не удалось прочитать задачи", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun updateTasks(change: (MutableList<String>) -> Unit): Boolean {
        val updated = ArrayList(tasks)
        change(updated)
        val bytes = JSONArray(updated).toString().toByteArray(Charsets.UTF_8)
        try {
            val stream = tasksFile.startWrite()
            try {
                stream.write(bytes)
                tasksFile.finishWrite(stream)
            } catch (error: IOException) {
                tasksFile.failWrite(stream)
                throw error
            }
        } catch (error: IOException) {
            Toast.makeText(this, "Не удалось сохранить задачи", Toast.LENGTH_LONG).show()
            return false
        }
        tasks.clear()
        tasks.addAll(updated)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val login = intent.getStringExtra(ProfileActivity.EXTRA_LOGIN)
        if (login.isNullOrBlank()) {
            finish()
            return
        }
        val userKey = java.util.UUID.nameUUIDFromBytes(login.toByteArray(Charsets.UTF_8))
        tasksFile = AtomicFile(File(filesDir, "tasks_$userKey.json"))

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.listView)
        val userData: EditText = findViewById(R.id.user_data)
        val button: Button = findViewById(R.id.button)

        try {
            loadTasks()
        } catch (error: IOException) {
            showLoadError()
            return
        } catch (error: org.json.JSONException) {
            showLoadError()
            return
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        listView.adapter = adapter
        listView.emptyView = findViewById(R.id.tasksEmpty)

        listView.setOnItemClickListener { _, _, position, _ ->
            val input = EditText(this).apply {
                setSingleLine(true)
                setText(tasks[position])
                setSelection(text.length)
            }

            val dialog = AlertDialog.Builder(this)
                .setTitle("Редактировать задачу")
                .setView(input)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Сохранить", null)
                .show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newTitle = input.text.toString().trim()
                if (newTitle.isEmpty()) {
                    input.error = "Введите название задачи"
                    return@setOnClickListener
                }

                if (updateTasks { it[position] = newTitle }) {
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val task = adapter.getItem(position)
                ?: return@setOnItemLongClickListener false

            AlertDialog.Builder(this)
                .setTitle("Удалить задачу?")
                .setMessage(task)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Удалить"){_, _ ->
                    if (updateTasks { it.removeAt(position) }) {
                        adapter.notifyDataSetChanged()
                    }
                }
                .show()
            true
        }

        button.setOnClickListener {
            val text = userData.text.toString().trim()
            if (text.isEmpty()) {
                userData.error = "Введите название задачи"
                return@setOnClickListener
            }

            userData.error = null
            if (updateTasks { it.add(0, text) }) {
                adapter.notifyDataSetChanged()
                userData.text.clear()
            }
        }

    }
}
