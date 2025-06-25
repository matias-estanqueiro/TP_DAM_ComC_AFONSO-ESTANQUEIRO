package com.example.gymapp.ui.payments

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.view.View
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.R
import com.example.gymapp.adapters.PendingPaymentsListAdapter
import com.example.gymapp.data.dt.DtPendingPayment
import com.example.gymapp.data.payments.DaoPayment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewPendingPaymentsActivity : AppCompatActivity() {
    private lateinit var listPendingPayments: ListView
    private lateinit var pendingPaymentsAdapter: PendingPaymentsListAdapter
    private lateinit var daoPayment: DaoPayment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_pending_payments)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_pending_payments)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        daoPayment = (application as FitnessSportsApp).daoPayment
        listPendingPayments = findViewById(R.id.listViewPendingPayments)

        pendingPaymentsAdapter = PendingPaymentsListAdapter(this, ArrayList())
        listPendingPayments.adapter = pendingPaymentsAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val pendingPayments: List<DtPendingPayment>
            withContext(Dispatchers.IO) {
                pendingPayments = daoPayment.getPendingPayments()
            }

            withContext(Dispatchers.Main) {
                if (pendingPayments.isEmpty()) {
                    listPendingPayments.visibility = View.GONE
                } else {
                    listPendingPayments.visibility = View.VISIBLE
                    pendingPaymentsAdapter.updateData(pendingPayments)
                }
            }
        }
    }
}