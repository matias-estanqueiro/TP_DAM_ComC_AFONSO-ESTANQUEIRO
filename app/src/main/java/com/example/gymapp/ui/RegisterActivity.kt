package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.MainActivity
import com.example.gymapp.R
import com.example.gymapp.data.DaoUser
import com.example.gymapp.ui.LoginActivity.Companion.LOGIN_SUCCESS_MESSAGE
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.encryptPassword
import com.example.gymapp.utils.isValidEmail

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.gymapp.utils.setupNavigation
import com.example.gymapp.utils.showCustomSnackbar
import com.example.gymapp.utils.RegistrationResult

class RegisterActivity : AppCompatActivity() {
    private lateinit var userDao: DaoUser

    companion object {
        const val REGISTER_SUCCESS_MESSAGE = "register_success_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.inpUserEmail)
        val passInput = findViewById<EditText>(R.id.inpUserPassword)
        val passConfirmInput = findViewById<EditText>(R.id.inpConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val registerEmail = emailInput.text.toString().trim()
            val registerPass = passInput.text.toString().trim()
            val confirmPass = passConfirmInput.text.toString().trim()

            userDao = (application as FitnessSportsApp).daoUser

            // Validations
            // 1. All inputs are required
            // 2. Email valid format
            // 3. Email registered
            // 4. Password length > eight characters
            // 5. Equal Passwords (enter and confirm)
            if (registerEmail.isEmpty() || registerPass.isEmpty() || confirmPass.isEmpty()) {
                showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if(!isValidEmail(registerEmail)) {
                showCustomSnackbar(this, R.string.APP_email_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (registerPass.length < 8) {
                showCustomSnackbar(this, R.string.APP_password_length_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (registerPass != confirmPass) {
                showCustomSnackbar(this, R.string.APP_register_equal_password, SnackbarType.ERROR)
                return@setOnClickListener
            }

            val hashedPassword = encryptPassword(registerPass)
            CoroutineScope(Dispatchers.IO).launch {
                val registrationResult = userDao.registerUser(registerEmail, hashedPassword)
                withContext(Dispatchers.Main) {
                    when (registrationResult) {
                        RegistrationResult.SUCCESS -> {
                            val intent = Intent(this@RegisterActivity, DashboardActivity::class.java).apply {
                                putExtra(REGISTER_SUCCESS_MESSAGE, R.string.APP_register_success)
                            }
                            startActivity(intent)
                            finish()
                        }
                        RegistrationResult.EMAIL_EXISTS -> {
                            showCustomSnackbar(this@RegisterActivity, registrationResult.messageResId, SnackbarType.ERROR)
                        }
                        RegistrationResult.DB_ERROR -> {
                            showCustomSnackbar(this@RegisterActivity, registrationResult.messageResId, SnackbarType.ERROR)
                        }
                    }
                }
            }
        }

        setupNavigation(findViewById(R.id.btnBack), MainActivity::class.java)
    }
}