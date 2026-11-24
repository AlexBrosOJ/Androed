package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction

class TasksDb(
    context: Context,
    userKey: String,
    private val readJson: () -> List<String>
) : SQLiteOpenHelper(context, "tasks_" + userKey + ".db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE tasks (position INTEGER PRIMARY KEY, title TEXT NOT NULL)")
        insertTasks(db, readJson())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        error("Нужно обновить схему базы")
    }

    fun load(): List<String> {
        val tasks = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT title FROM tasks ORDER BY position", null).use { cursor ->
            while (cursor.moveToNext()) {
                tasks.add(cursor.getString(0))
            }
        }
        return tasks
    }

    fun save(tasks: List<String>) {
        writableDatabase.transaction {
            execSQL("DELETE FROM tasks")
            insertTasks(this, tasks)
        }
    }

    private fun insertTasks(db: SQLiteDatabase, tasks: List<String>) {
        tasks.forEachIndexed { position, title ->
            db.execSQL("INSERT INTO tasks (position, title) VALUES (?, ?)", arrayOf<Any>(position, title))
        }
    }
}
