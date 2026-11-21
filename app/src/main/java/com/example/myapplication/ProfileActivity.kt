package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LOGIN = "com.example.myapplication.LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val login = intent.getStringExtra(EXTRA_LOGIN)
        if (login.isNullOrBlank()) {
            openLogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileScroll)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        findViewById<TextView>(R.id.profileLogin).text = login
        findViewById<Button>(R.id.profileTasks).setOnClickListener {
            val tasksIntent = Intent(this, MainActivity::class.java)
                .putExtra(EXTRA_LOGIN, login)
            startActivity(tasksIntent)
        }
        findViewById<Button>(R.id.profileLogout).setOnClickListener {
            openLogin()
        }
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
