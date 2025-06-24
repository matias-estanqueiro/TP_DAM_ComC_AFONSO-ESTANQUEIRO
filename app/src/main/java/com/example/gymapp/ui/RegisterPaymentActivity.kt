package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.LinearLayout
import android.widget.RadioGroup
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.R
import com.example.gymapp.data.dao.DaoActivity
import com.example.gymapp.data.dao.DaoClient
import com.example.gymapp.data.dao.DaoMembership
import com.example.gymapp.data.dao.DaoPaymentType
import com.example.gymapp.data.dt.DtActivity
import com.example.gymapp.data.dt.DtClient
import com.example.gymapp.data.dt.DtPaymentType
import com.example.gymapp.data.payments.DtPayment
import com.example.gymapp.data.payments.DtPaymentActivity
import com.example.gymapp.data.payments.DtPaymentMembership
import com.example.gymapp.utils.ActionResult
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.isValidOnlyNumbers
import com.example.gymapp.utils.showCustomSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterPaymentActivity : AppCompatActivity() {

    // Both views
    private lateinit var rdgSelectClient: RadioGroup
    private lateinit var rdbPaymentMember: RadioButton
    private lateinit var btnNext: Button

    // Member form views
    private lateinit var frmPaymentMember: LinearLayout
    private lateinit var inpMemberDni: EditText
    private lateinit var spnPaymentType: Spinner
    private lateinit var rdgPaymentInstallments: RadioGroup

    // Client form views
    private lateinit var frmPaymentClient: LinearLayout
    private lateinit var inpClientDni: EditText
    private lateinit var spnClientClass: Spinner

    // DAOs
    private lateinit var daoClient: DaoClient
    private lateinit var daoActivity: DaoActivity
    private lateinit var daoPaymentType: DaoPaymentType
    private lateinit var daoMembership: DaoMembership

    private var currentValidatedClient: DtClient? = null
    private var paymentTypes: List<DtPaymentType> = emptyList()
    private var activities: List<DtActivity> = emptyList()

    companion object {
        const val PAYMENT_DATA = "payment_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_payment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        daoClient = (application as FitnessSportsApp).daoClient
        daoMembership = (application as FitnessSportsApp).daoMembership
        daoPaymentType = (application as FitnessSportsApp).daoPaymentType

        rdgSelectClient = findViewById(R.id.rdgSelectClient)
        rdbPaymentMember = findViewById(R.id.rdbPaymentMember)
        btnNext = findViewById(R.id.btnNext)
        frmPaymentMember = findViewById(R.id.frmPaymentMember)
        inpMemberDni = findViewById(R.id.inpMemberDni)
        spnPaymentType = findViewById(R.id.spnPaymentType)
        rdgPaymentInstallments = findViewById(R.id.rdgPaymentInstallments)
        frmPaymentClient = findViewById(R.id.frmPaymentClient)
        inpClientDni = findViewById(R.id.inpClientDni)
        spnClientClass = findViewById(R.id.spnClientClass)

        daoClient = (application as FitnessSportsApp).daoClient
        daoActivity = (application as FitnessSportsApp).daoActivity
        daoPaymentType = (application as FitnessSportsApp).daoPaymentType

        currentValidatedClient = null

        rdgSelectClient.setOnCheckedChangeListener { _, checkedId ->
            currentValidatedClient = null
            inpMemberDni.setText("")
            inpClientDni.setText("")
            when (checkedId) {
                R.id.rdbPaymentMember -> {
                    frmPaymentMember.visibility = View.VISIBLE
                    frmPaymentClient.visibility = View.GONE
                    inpClientDni.setText("")
                    if (spnPaymentType.adapter != null && spnPaymentType.adapter.count > 0) {
                        spnPaymentType.setSelection(0)
                    } else {
                        rdgPaymentInstallments.visibility = View.GONE
                        rdgPaymentInstallments.clearCheck()
                    }
                }

                R.id.rdbPaymentClient -> {
                    frmPaymentMember.visibility = View.GONE
                    frmPaymentClient.visibility = View.VISIBLE
                    inpMemberDni.setText("")
                    rdgPaymentInstallments.visibility = View.GONE
                    rdgPaymentInstallments.clearCheck()
                }
            }
        }
        loadPaymentTypesIntoSpinner()
        loadActivitiesIntoSpinner()
        rdgSelectClient.check(R.id.rdbPaymentMember)

        btnNext.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                handleDataPayment()
            }
        }
    }

    /**
     * Loads the payment types from the database into the Spinner spnPaymentType.
     */
    private fun loadPaymentTypesIntoSpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            paymentTypes = daoPaymentType.getAllPaymentTypes()
            withContext(Dispatchers.Main) {
                val paymentTypeNames = paymentTypes.map { it.description }
                val adapter = ArrayAdapter(
                    this@RegisterPaymentActivity,
                    android.R.layout.simple_spinner_item,
                    paymentTypeNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnPaymentType.adapter = adapter
                spnPaymentType.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedPaymentType = parent?.getItemAtPosition(position).toString()
                            val creditCardString = getString(R.string.PAYMENT_TYPE_CREDIT_CARD)
                            if (selectedPaymentType == creditCardString) {
                                rdgPaymentInstallments.visibility = View.VISIBLE
                                rdgPaymentInstallments.check(R.id.rdbOnePayment)
                            } else {
                                rdgPaymentInstallments.visibility = View.GONE
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            rdgPaymentInstallments.visibility = View.GONE
                            rdgPaymentInstallments.clearCheck()
                        }
                    }
                if (paymentTypeNames.isNotEmpty()) {
                    spnPaymentType.setSelection(0)
                } else {
                    rdgPaymentInstallments.visibility = View.GONE
                    rdgPaymentInstallments.clearCheck()
                }
            }
        }
    }

    /**
     * Loads the names of the activities from the database into the Spinner spnClientClass.
     */
    private fun loadActivitiesIntoSpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            activities = daoActivity.getAllActivities()
            withContext(Dispatchers.Main) {
                val activityNames = activities.map { it.name }
                val adapter = ArrayAdapter(
                    this@RegisterPaymentActivity,
                    android.R.layout.simple_spinner_item,
                    activityNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnClientClass.adapter = adapter
            }
        }
    }

    /**
     * Main function to handle the payment data collection and delegation.
     * It calls the specific handler based on the selected client type.
     */
    private suspend fun handleDataPayment() {
        val dni: String
        val isMemberFormSelected = rdbPaymentMember.isChecked
        dni = (if (isMemberFormSelected) inpMemberDni else inpClientDni).text.toString().trim()

        // Validations
        // 1. All fields required
        // 2. DNI can only have numbers
        // 3. DNI must exists in DB
        if (dni.isEmpty()) {
            showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
            return
        }
        if (!isValidOnlyNumbers(dni)) {
            showCustomSnackbar(this, R.string.CLIENT_dni_validation, SnackbarType.ERROR)
            return
        }
        val clientExists = withContext(Dispatchers.IO) {
            daoClient.checkClientDNI(dni)
        }
        if (!clientExists) {
            showCustomSnackbar(this, ActionResult.NOT_FOUND.messageResId, SnackbarType.ERROR)
            return
        }
        val memberClient = withContext(Dispatchers.IO) {
            daoClient.getMemberByDni(dni)
        }
        val regularClient = withContext(Dispatchers.IO) {
            daoClient.getClientByDni(dni)
        }
        if (isMemberFormSelected) {
            if (memberClient != null) {
                currentValidatedClient = memberClient
                handlePaymentMember(memberClient)
            } else {
                showCustomSnackbar(this, ActionResult.ERROR.messageResId, SnackbarType.ERROR)
                currentValidatedClient = null
            }
        } else {
            if (regularClient != null) {
                currentValidatedClient = regularClient
                handlePaymentClient(regularClient)
            } else {
                showCustomSnackbar(this, ActionResult.ERROR.messageResId, SnackbarType.ERROR)
                currentValidatedClient = null
            }
        }
    }

    /**
     * Handles the data collection and validation for a Member (membership) payment.
     * This function will later call navigateToPaymentVoucher.
     * @param member The validated DtClient object for the member.
     */
    private suspend fun handlePaymentMember(member: DtClient) {
        val plan = withContext(Dispatchers.IO) {
            daoMembership.getMembershipById(member.plan)
        }
        val selectedPaymentType = spnPaymentType.selectedItem?.toString() ?: ""
        val methodPaymentId: Int =  (paymentTypes.find { it.description == selectedPaymentType }?.id)!!.toInt()
        val creditCardString = getString(R.string.PAYMENT_TYPE_CREDIT_CARD)
        var numberOfInstallments = 1
        if (selectedPaymentType == creditCardString) {
            numberOfInstallments = when (rdgPaymentInstallments.checkedRadioButtonId) {
                R.id.rdbOnePayment -> 1
                R.id.rdbThreePayments -> 3
                R.id.rdbSixPayments -> 6
                else -> {
                    return
                }
            }
        }
        val dueDate: Date = calculatePaymentDueDate(numberOfInstallments)
        val amount = if (selectedPaymentType == creditCardString) {
            plan?.price?.times(numberOfInstallments)!!.toInt()
        } else {
            plan?.price!!.toInt()
        }
        val paymentMemberData = DtPaymentMembership(
                client = member,
                methodPaymentId = methodPaymentId,
                date = Date(),
                membershipId = member.plan,
                dueDate = dueDate,
                membershipName = member.planName.toString(),
                amount = amount,
                methodPaymentName = selectedPaymentType,
                installments = numberOfInstallments
            )
        navigateToPaymentVoucher(paymentMemberData)
    }

    /**
     * Handles the data collection and validation for a Client (activity) payment.
     * This function will later call navigateToPaymentVoucher.
     */
    private fun handlePaymentClient(client: DtClient) {
        val methodPaymentId: Int = (paymentTypes.find { it.description == "CASH" }?.id)!!.toInt()
        val selectedActivity = spnClientClass.selectedItem?.toString() ?: ""
        val activityId: Int = (activities.find {it.name == selectedActivity})?.id!!.toInt()
        val activityPrice: Int = (activities.find {it.id == activityId})?.price!!.toInt()

        val paymentClientData = DtPaymentActivity(
            client = client,
            methodPaymentId = methodPaymentId,
            date = Date(),
            activityId = activityId,
            activityName = selectedActivity,
            activityPrice = activityPrice
        )
        navigateToPaymentVoucher(paymentClientData)
    }

    /**
     * Navigates to ViewPaymentVoucherActivity, passing all payment information.
     * Now accepts the common DtPayment interface.
     * @param paymentData The DtPayment object containing all payment details.
     */
    private fun navigateToPaymentVoucher(paymentData: DtPayment) {
        val intent = Intent(this, ViewPaymentVoucherActivity::class.java).apply {
            putExtra(PAYMENT_DATA, paymentData)
        }
        startActivity(intent)
    }

    /**
     * Calculates the payment due date based on the number of installments.
     *
     * @param numberOfInstallments The number of installments for the payment. Must be a positive integer.
     * If 1, the due date is the last day of the current month.
     * If greater than 1, that number of months is added.
     * @return A [Date] object representing the calculated due date.
     */
    private fun calculatePaymentDueDate(numberOfInstallments: Int): Date {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        if (numberOfInstallments > 1) {
            calendar.add(Calendar.MONTH, numberOfInstallments)
        }
        return calendar.time
    }
}
