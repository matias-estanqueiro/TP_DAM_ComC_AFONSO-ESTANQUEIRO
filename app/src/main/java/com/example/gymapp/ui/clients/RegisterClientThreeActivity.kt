package com.example.gymapp.ui.clients

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView

import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.R
import com.example.gymapp.data.dao.DaoClient
import com.example.gymapp.data.dt.DtClient
import com.example.gymapp.data.dao.DaoMembership
import com.example.gymapp.data.dt.DtMembership
import com.example.gymapp.ui.clients.RegisterClientFirstActivity.Companion.CLIENT_DATA
import com.example.gymapp.utils.ActionResult
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.showCustomSnackbar
import com.example.gymapp.utils.isValidOnlyNumbers
import com.example.gymapp.utils.isValidEmail

class RegisterClientThreeActivity : AppCompatActivity() {
    private lateinit var clientDao: DaoClient
    private lateinit var membershipDao: DaoMembership

    private lateinit var btnBack : Button
    private lateinit var btnRegister : Button
    private lateinit var inputClientPhone : EditText
    private lateinit var inputClientEmail : EditText
    private lateinit var rdgClientType : RadioGroup
    private lateinit var rdbMember: RadioButton
    private lateinit var rdbClient: RadioButton
    private lateinit var lblMembershipType : TextView
    private lateinit var spnMembershipType : Spinner
    private lateinit var vwMemberShipType : View

    private var currentClient: DtClient = DtClient()
    private var allMemberships: List<DtMembership> = emptyList()

    companion object {
        const val REGISTER_SUCCESS_MESSAGE = "register_success_message"
        private const val NO_PLAN_ID = 1
        private const val NO_PLAN_NAME = "NO PLAN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_client_three)

        btnBack = findViewById(R.id.btnBack)
        btnRegister = findViewById(R.id.btnRegister)
        inputClientPhone = findViewById(R.id.inpClientPhone)
        inputClientEmail = findViewById(R.id.inpClientEmail)
        rdgClientType = findViewById(R.id.rdgClientType)
        rdbMember = findViewById(R.id.rdbMember)
        rdbClient = findViewById(R.id.rdbClient)
        lblMembershipType = findViewById(R.id.lblMembershipType)
        spnMembershipType = findViewById(R.id.spnMembershipType)
        vwMemberShipType = findViewById(R.id.vwMemberShipType)

        clientDao = (application as FitnessSportsApp).daoClient
        membershipDao = (application as FitnessSportsApp).daoMembership

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_client_three)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState != null) {
            currentClient = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable(CLIENT_DATA, DtClient::class.java) ?: DtClient()
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getParcelable(CLIENT_DATA) ?: DtClient()
            }
        } else {
            val clientFromIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(CLIENT_DATA, DtClient::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(CLIENT_DATA)
            }
            clientFromIntent?.let {
                currentClient = it
            }
        }

        loadMembershipsIntoSpinner()
        updateUIWithClientData(currentClient)

        rdgClientType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdbMember -> {
                    lblMembershipType.visibility = View.VISIBLE
                    spnMembershipType.visibility = View.VISIBLE
                    vwMemberShipType.visibility = View.VISIBLE
                    if (currentClient.plan == NO_PLAN_ID) {
                        if (allMemberships.isNotEmpty()) {
                            val mostExpensivePlan = allMemberships.maxByOrNull { it.price }
                            val defaultSelectionIndex = allMemberships.indexOf(mostExpensivePlan)
                            if (defaultSelectionIndex != -1) {
                                spnMembershipType.setSelection(defaultSelectionIndex)
                            }
                        }
                    } else {
                        val planIndex = allMemberships.indexOfFirst { it.id == currentClient.plan }
                        if (planIndex != -1) {
                            spnMembershipType.setSelection(planIndex)
                        }
                    }
                }
                R.id.rdbClient -> {
                    lblMembershipType.visibility = View.GONE
                    spnMembershipType.visibility = View.GONE
                    vwMemberShipType.visibility = View.GONE
                }
            }
        }

        updateMembershipVisibility(getSelectedClientType())

        btnBack.setOnClickListener {
            currentClient.phone = inputClientPhone.text.toString().trim()
            currentClient.email = inputClientEmail.text.toString().trim()
            currentClient.type = getSelectedClientType()

            val backIntent = Intent(this, RegisterClientTwoActivity::class.java).apply {
                putExtra(CLIENT_DATA, currentClient)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(backIntent)
            finish()
        }

        btnRegister.setOnClickListener {
            val clientPhone = inputClientPhone.text.toString().trim()
            val clientEmail = inputClientEmail.text.toString().trim()
            val clientType = getSelectedClientType()

            // Validations
            // 1. All fields required
            // 2. Phone can only have numbers
            // 3. Email client format
            if (clientPhone.isEmpty() || clientEmail.isEmpty()) {
                showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (!isValidOnlyNumbers(clientPhone)) {
                showCustomSnackbar(this, R.string.CLIENT_phone_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (!isValidEmail(clientEmail)) {
                showCustomSnackbar(this, R.string.APP_email_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            currentClient.phone = clientPhone
            currentClient.email = clientEmail
            currentClient.type = clientType
            assignClientPlan()

            CoroutineScope(Dispatchers.IO).launch {
                val actionResult = clientDao.registerClient(currentClient)
                withContext(Dispatchers.Main) {
                    when (actionResult) {
                        ActionResult.SUCCESS -> {
                            val intent = Intent(this@RegisterClientThreeActivity, RegisterClientFirstActivity::class.java).apply {
                                putExtra(REGISTER_SUCCESS_MESSAGE, actionResult.messageResId)
                            }
                            startActivity(intent)
                            finish()
                        }
                        ActionResult.ERROR -> {
                            showCustomSnackbar(this@RegisterClientThreeActivity, actionResult.messageResId, SnackbarType.ERROR)
                            return@withContext
                        }
                        ActionResult.DATA_EXISTS -> {
                            showCustomSnackbar(this@RegisterClientThreeActivity, actionResult.messageResId, SnackbarType.ERROR)
                            return@withContext
                        }
                        ActionResult.NOT_FOUND -> {
                            showCustomSnackbar(this@RegisterClientThreeActivity, actionResult.messageResId, SnackbarType.ERROR)
                        }
                    }

                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val clientFromNewIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CLIENT_DATA, DtClient::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(CLIENT_DATA)
        }

        clientFromNewIntent?.let {
            currentClient = it
            updateUIWithClientData(it)
            if (it.type == 1 && it.plan != NO_PLAN_ID && allMemberships.isNotEmpty()) {
                val planIndex = allMemberships.indexOfFirst { mem -> mem.id == it.plan }
                if (planIndex != -1) {
                    spnMembershipType.setSelection(planIndex)
                }
            }
        }
    }

    /**
     * Saves the state of the ‘currentClient’ object if the Activity will be destroyed temporarily (e.g. by screen rotation).
     * temporarily (e.g. by screen rotation).
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CLIENT_DATA, currentClient)
    }

    /**
     * Auxiliary function to update the UI with the Client object data.
     */
    private fun updateUIWithClientData(client: DtClient?) {
        client?.let {
            inputClientPhone.setText(client.phone)
            inputClientEmail.setText(client.email)
            when (client.type) {
                1 -> rdbMember.isChecked = true
                0 -> rdbClient.isChecked = true
                else -> {
                    rdbClient.isChecked = true
                }
            }
        }
    }

    /**
     * Gets the value of the customer type (0 or 1) from the RadioGroup.
     * Returns -1 if no RadioButton is selected.
     */
    private fun getSelectedClientType(): Int {
        return when (rdgClientType.checkedRadioButtonId) {
            R.id.rdbMember -> 1
            R.id.rdbClient -> 0
            else -> -1
        }
    }

    /**
     * Updates the visibility of the membership spinner and its label based on client type.
     */
    private fun updateMembershipVisibility(clientType: Int) {
        if (clientType == 1) {
            lblMembershipType.visibility = View.VISIBLE
            spnMembershipType.visibility = View.VISIBLE
            vwMemberShipType.visibility = View.VISIBLE
        } else {
            lblMembershipType.visibility = View.GONE
            spnMembershipType.visibility = View.GONE
            vwMemberShipType.visibility = View.GONE
        }
    }

    /**
     * Load the names of the memberships from the database into the Spinner.
     * Select the most expensive plan by default.
     */
    private fun loadMembershipsIntoSpinner() {
        CoroutineScope(Dispatchers.IO).launch {

            val filteredMemberships = membershipDao.getAllMemberships().filter { it.id != NO_PLAN_ID }
            withContext(Dispatchers.Main) {
                allMemberships = filteredMemberships

                val membershipNames = filteredMemberships.map { it.name }

                val adapter = ArrayAdapter(
                    this@RegisterClientThreeActivity,
                    android.R.layout.simple_spinner_item,
                    membershipNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnMembershipType.adapter = adapter

                if (filteredMemberships.isNotEmpty()) {
                    val mostExpensivePlan = filteredMemberships.maxByOrNull { it.price }
                    val defaultSelectionIndex = filteredMemberships.indexOf(mostExpensivePlan)
                    if (defaultSelectionIndex != -1) {
                        spnMembershipType.setSelection(defaultSelectionIndex)
                    }
                }


                if (currentClient.type == 1) {
                    val planIndex = allMemberships.indexOfFirst { it.id == currentClient.plan }
                    if (planIndex != -1) {
                        spnMembershipType.setSelection(planIndex)
                    }
                }
            }
        }
    }

    /**
     * Assigns the plan ID to the currentClient based on the selected client type
     * and the Spinner value if applicable.
     */
    private fun assignClientPlan() {
        currentClient.plan = NO_PLAN_ID
        currentClient.planName = NO_PLAN_NAME

        val clientType = getSelectedClientType()
        if (clientType == 1) {
            val selectedPlanName = spnMembershipType.selectedItem as? String
            if (selectedPlanName != null) {
                val selectedMembership = allMemberships.find { it.name == selectedPlanName }
                if (selectedMembership != null) {
                    currentClient.plan = selectedMembership.id!!
                    currentClient.planName = selectedMembership.name
                }
            }
        }
    }
}