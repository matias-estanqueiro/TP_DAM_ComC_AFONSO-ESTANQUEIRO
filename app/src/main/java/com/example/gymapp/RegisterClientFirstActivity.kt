package com.example.gymapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.gymapp.utils.setupNavigation
import android.widget.CheckBox
import android.widget.LinearLayout
import android.view.View
import android.widget.Button

class RegisterClientFirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_client_first)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_client_first)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val checkFitnessCertificate = findViewById<CheckBox>(R.id.chkFitnessCertificate)
        val formRegisterClient = findViewById<LinearLayout>(R.id.frmClientRegister)
        val buttonNext = findViewById<Button>(R.id.btnNext)

        checkFitnessCertificate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                formRegisterClient.visibility = View.VISIBLE
                buttonNext.isEnabled = true

            } else {
                formRegisterClient.visibility = View.GONE
                buttonNext.isEnabled = false
            }
        }

        setupNavigation(findViewById(R.id.btnNext), RegisterClientTwoActivity::class.java)
    }
}