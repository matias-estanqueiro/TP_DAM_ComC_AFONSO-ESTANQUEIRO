package com.example.gymapp.ui


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.R
import com.example.gymapp.data.payments.DaoPayment
import com.example.gymapp.data.payments.DtPayment
import com.example.gymapp.data.payments.DtPaymentActivity
import com.example.gymapp.data.payments.DtPaymentMembership
import com.example.gymapp.ui.RegisterClientThreeActivity.Companion.REGISTER_SUCCESS_MESSAGE
import com.example.gymapp.ui.RegisterPaymentActivity.Companion.PAYMENT_DATA
import com.example.gymapp.utils.ActionResult
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.setupNavigation
import com.example.gymapp.utils.showCustomSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class ViewPaymentVoucherActivity : AppCompatActivity() {
    companion object {
        const val PAYMENT_SUCCESS_MESSAGE = "payment_success_message"
    }

    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var daoPayment: DaoPayment
    private lateinit var dateFormatted: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_payment_voucher)

        val lblDni: TextView = findViewById(R.id.lblDni)
        val lblNameSurname: TextView = findViewById(R.id.lblNameSurname)
        val lblDate: TextView = findViewById(R.id.lblPaymentDay)
        val lblPaymentMethod: TextView = findViewById(R.id.lblPaymentType)
        val lblPaymentValue: TextView = findViewById(R.id.lblPaymentValue)
        val lblPaymentInstallments: TextView = findViewById(R.id.lblPaymentInstallments)
        val lblPaymentType: TextView = findViewById(R.id.lblPaymentPlanActivity)
        val btnBack: Button = findViewById(R.id.btnBack)
        val btnRegister: Button = findViewById(R.id.btnRegister)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_voucher_payment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val paymentData: DtPayment? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PAYMENT_DATA, DtPayment::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(PAYMENT_DATA) as? DtPayment
        }

        dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        daoPayment = (application as FitnessSportsApp).daoPayment

        dateFormatted = dateFormat.format(paymentData?.date!!)

        when(paymentData) {
            is DtPaymentMembership -> {
                lblDni.text = getString(R.string.VOUCHER_dni, paymentData.client.dni)
                lblNameSurname.text = getString(R.string.FORMAT_full_name, paymentData.client.surname, paymentData.client.name)
                lblDate.text = dateFormatted
                lblPaymentMethod.text = getString(R.string.VOUCHER_payment_method, paymentData.methodPaymentName)
                lblPaymentValue.text = getString(R.string.FORMAT_price, paymentData.amount)
                lblPaymentInstallments.text = getString(R.string.VOUCHER_installments, paymentData.installments)
                lblPaymentType.text = getString(R.string.VOUCHER_membership_name, paymentData.client.planName)
            }

            is DtPaymentActivity -> {
                lblDni.text = getString(R.string.VOUCHER_dni, paymentData.client.dni)
                lblNameSurname.text = getString(R.string.FORMAT_full_name, paymentData.client.surname, paymentData.client.name)
                lblDate.text =dateFormatted
                lblPaymentType.text = getString(R.string.VOUCHER_activity_name, paymentData.activityName)
                lblPaymentValue.text = getString(R.string.FORMAT_price, paymentData.activityPrice)
            }
        }

        btnRegister.setOnClickListener {
            confirmAndSavePayment(paymentData)
        }
        setupNavigation(btnBack, RegisterPaymentActivity::class.java)
    }

    /**
     * Confirms the payment and registers it in the database.
     * @param paymentData The DtPayment object to register.
     */
    private fun confirmAndSavePayment(paymentData: DtPayment) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                when(paymentData) {
                    is DtPaymentMembership -> {
                        val dueDateFormatted = dateFormat.format(paymentData.dueDate)
                        daoPayment.registerMemberPayment(
                            membershipId = paymentData.membershipId,
                            paymentMethodId = paymentData.methodPaymentId,
                            clientId = paymentData.client.id!!,
                            amount = paymentData.amount,
                            paymentDate = dateFormatted,
                            dueDate = dueDateFormatted
                        )
                    }
                    is DtPaymentActivity -> {
                        daoPayment.registerActivityPayment(
                            activityId = paymentData.activityId,
                            paymentMethodId = paymentData.methodPaymentId,
                            clientId = paymentData.client.id!!,
                            paymentDate = dateFormatted
                        )
                    }
                    else -> ActionResult.ERROR
                }
            }
            if (result == ActionResult.SUCCESS) {
                val intent = Intent(this@ViewPaymentVoucherActivity, DashboardActivity::class.java).apply {
                    putExtra(REGISTER_SUCCESS_MESSAGE, result.messageResId)
                }
                startActivity(intent)
                finish()
            } else {
                showCustomSnackbar(this@ViewPaymentVoucherActivity, result.messageResId, SnackbarType.ERROR)
            }
        }
    }
}
