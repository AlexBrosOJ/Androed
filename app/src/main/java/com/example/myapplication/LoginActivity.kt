package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    var usersDatabase = mutableMapOf<String, String>()

    private fun validateCredentials(login: String, password: String): Boolean {
        if (login.isNotBlank() && password.isNotBlank()) return true

        Toast.makeText(this, "Заполните логин и пароль!", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etLogin: EditText = findViewById(R.id.e1)
        val etPassword: EditText = findViewById(R.id.p1)
        val btnRegister: Button = findViewById(R.id.button3)
        val btnLogin: Button = findViewById(R.id.button2)

        btnRegister.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateCredentials(login, password)) {
                usersDatabase[login] = password
                Toast.makeText(this, "Пользователь $login успешно сохранен!", Toast.LENGTH_SHORT).show()

                etLogin.text.clear()
                etPassword.text.clear()
            }
        }

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateCredentials(login, password)) {

                if (usersDatabase.containsKey(login) && usersDatabase[login] == password) {
                    Toast.makeText(this, "Успешный вход!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ProfileActivity::class.java)
                        .putExtra(ProfileActivity.EXTRA_LOGIN, login)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
