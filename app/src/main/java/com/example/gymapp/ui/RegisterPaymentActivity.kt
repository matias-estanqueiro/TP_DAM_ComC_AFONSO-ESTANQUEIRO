package com.example.gymapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.gymapp.utils.setupNavigation
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.view.View
import com.example.gymapp.R

class RegisterPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_payment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val radioGroupSelectClient = findViewById<RadioGroup>(R.id.rdgSelectClient)
        val formPaymentMember = findViewById<LinearLayout>(R.id.frmPaymentMember)
        val formPaymentClient = findViewById<LinearLayout>(R.id.frmPaymentClient)

        radioGroupSelectClient.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdbPaymentMember -> {
                    formPaymentMember.visibility = View.VISIBLE
                    formPaymentClient.visibility = View.GONE
                }
                R.id.rdbPaymentClient -> {
                    formPaymentMember.visibility = View.GONE
                    formPaymentClient.visibility = View.VISIBLE
                }
            }
        }

        setupNavigation(findViewById(R.id.btnNext), ViewPaymentVoucherActivity::class.java)
    }
}