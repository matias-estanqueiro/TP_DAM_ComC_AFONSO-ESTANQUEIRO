package com.example.gymapp.ui

import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.MainActivity
import com.example.gymapp.R
import android.widget.EditText
import android.widget.Button

import com.example.gymapp.utils.setupNavigation
import com.example.gymapp.utils.encryptPassword
import com.example.gymapp.data.FitnessSportsDbHelper
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.isValidEmail
import com.example.gymapp.utils.showCustomSnackbar

class LoginActivity : AppCompatActivity() {
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

        val dbHelper = FitnessSportsDbHelper(this)

        btnLogin.setOnClickListener {
            val userEmail = emailInput.text.toString().trim()
            val userPassword = passwordInput.text.toString().trim()

            // Validations
            // 1. All inputs are required
            // 2. Email format xxx@xxx.com
            // 2. Password length > eight characters
            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                showCustomSnackbar(this, R.string.APP_login_required_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if(!isValidEmail(userEmail)) {
                showCustomSnackbar(this, R.string.APP_login_invalid_email, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (userPassword.length < 8) {
                showCustomSnackbar(this, R.string.APP_login_password_length_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT password FROM users WHERE mail = ? LIMIT 1", arrayOf(userEmail))
            if (cursor.moveToFirst()) {
                val storedPassword = cursor.getString(0)
                val encryptedInput = encryptPassword(userPassword)
                if (encryptedInput == storedPassword) {
                    setupNavigation(btnLogin, DashboardActivity::class.java)
                } else {
                    showCustomSnackbar(this, R.string.APP_login_password_validation, SnackbarType.ERROR)
                }
            } else {
                showCustomSnackbar(this, R.string.APP_login_email_validation, SnackbarType.ERROR)
            }
            cursor.close()
            db.close()
        }
        setupNavigation(btnBack, MainActivity::class.java)
    }
}