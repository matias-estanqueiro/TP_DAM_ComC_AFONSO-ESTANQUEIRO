package com.example.gymapp.ui

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.MainActivity
import com.example.gymapp.R
import android.widget.EditText
import android.widget.Button
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.data.DaoUser

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.gymapp.utils.setupNavigation
import com.example.gymapp.utils.encryptPassword
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.isValidEmail
import com.example.gymapp.utils.showCustomSnackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var userDao: DaoUser

    companion object {
        const val LOGIN_SUCCESS_MESSAGE = "login_success_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.inpUserEmail)
        val passwordInput = findViewById<EditText>(R.id.inpUserPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<Button>(R.id.btnBack)

        userDao = (application as FitnessSportsApp).daoUser
        

        btnLogin.setOnClickListener {
            val userEmail = emailInput.text.toString().trim()
            val userPassword = passwordInput.text.toString().trim()
            val hashedPassword = encryptPassword(userPassword)

            // Validations
            // 1. All inputs are required
            // 2. Email format xxx@xxx.com
            // 2. Password length > eight characters
            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if(!isValidEmail(userEmail)) {
                showCustomSnackbar(this, R.string.APP_email_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (userPassword.length < 8) {
                showCustomSnackbar(this, R.string.APP_password_length_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            // Check Login
            CoroutineScope(Dispatchers.IO).launch {
                val userLogin = userDao.checkLogin(userEmail, hashedPassword)

                withContext(Dispatchers.Main) {
                    if (userLogin) {
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java).apply {
                            putExtra(LOGIN_SUCCESS_MESSAGE, R.string.APP_login_success)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        showCustomSnackbar(this@LoginActivity, R.string.APP_login_error, SnackbarType.ERROR)
                    }
                }
            }
        }
        setupNavigation(btnBack, MainActivity::class.java)
    }
}