package com.example.gymapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.gymapp.utils.setupNavigation

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigation(findViewById(R.id.btnRegisterClient), RegisterClientFirstActivity::class.java)
        setupNavigation(findViewById(R.id.btnRegisterPayment), RegisterPaymentActivity::class.java)
        setupNavigation(findViewById(R.id.btnRegisterClass), RegisterClassActivity::class.java)
        setupNavigation(findViewById(R.id.btnIdCardView), SearchCardIdActivity::class.java)
        setupNavigation(findViewById(R.id.btnPendingPayments), ViewPendingPaymentsActivity::class.java)

    }
}