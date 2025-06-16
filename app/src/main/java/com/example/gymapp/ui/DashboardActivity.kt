package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.R
import com.example.gymapp.utils.SnackbarType

import com.example.gymapp.utils.setupNavigation
import com.example.gymapp.utils.showCustomSnackbar

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

        val incomingIntent = intent
        handleIncomingMessage(incomingIntent, LoginActivity.LOGIN_SUCCESS_MESSAGE)
        handleIncomingMessage(incomingIntent, RegisterActivity.REGISTER_SUCCESS_MESSAGE)

        setupNavigation(findViewById(R.id.btnRegisterClient), RegisterClientFirstActivity::class.java)
        setupNavigation(findViewById(R.id.btnRegisterPayment), RegisterPaymentActivity::class.java)
        setupNavigation(findViewById(R.id.btnRegisterClass), RegisterClassActivity::class.java)
        setupNavigation(findViewById(R.id.btnIdCardView), SearchCardIdActivity::class.java)
        setupNavigation(findViewById(R.id.btnPendingPayments), ViewPendingPaymentsActivity::class.java)
    }

    /**
     * Auxiliary function to handle the extraction and display of messages from an Intent.
     * @param intent The Intent that can contain the message.
     * @param messageKey The key of the extra containing the ID of the message string resource.
     */
    private fun handleIncomingMessage(intent: Intent, messageKey: String) {
        if (intent.hasExtra(messageKey)) {
            val messageResId = intent.getIntExtra(messageKey, 0)
            if (messageResId != 0) {
                showCustomSnackbar(this@DashboardActivity, messageResId, SnackbarType.SUCCESS)
            }
            intent.removeExtra(messageKey)
        }
    }
}