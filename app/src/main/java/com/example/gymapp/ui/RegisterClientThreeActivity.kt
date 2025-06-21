package com.example.gymapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.R
import com.example.gymapp.data.DaoClient
import com.example.gymapp.data.DtClient
import com.example.gymapp.ui.RegisterClientFirstActivity.Companion.CLIENT_DATA
import com.example.gymapp.utils.RegistrationResult
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.showCustomSnackbar
import com.example.gymapp.utils.isValidOnlyNumbers
import com.example.gymapp.utils.isValidEmail

class RegisterClientThreeActivity : AppCompatActivity() {
    private lateinit var clientDao: DaoClient

    private lateinit var btnBack : Button
    private lateinit var btnRegister : Button
    private lateinit var inputClientPhone : EditText
    private lateinit var inputClientEmail : EditText
    private lateinit var rdgClientType : RadioGroup
    private lateinit var rdbMember: RadioButton
    private lateinit var rdbClient: RadioButton

    private var currentClient: DtClient = DtClient()

    companion object {
        const val REGISTER_SUCCESS_MESSAGE = "register_success_message"
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

        clientDao = (application as FitnessSportsApp).daoClient

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

        updateUIWithClientData(currentClient)

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

            if (clientType == -1) {
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

            CoroutineScope(Dispatchers.IO).launch {
                val registrationResult = clientDao.registerClient(currentClient)
                withContext(Dispatchers.Main) {
                    when (registrationResult) {
                        RegistrationResult.SUCCESS -> {
                            val intent = Intent(this@RegisterClientThreeActivity, DashboardActivity::class.java).apply {
                                putExtra(REGISTER_SUCCESS_MESSAGE, R.string.APP_register_success)
                            }
                            startActivity(intent)
                            finish()
                        }
                        RegistrationResult.EMAIL_EXISTS -> {
                            showCustomSnackbar(this@RegisterClientThreeActivity, registrationResult.messageResId, SnackbarType.ERROR)
                            return@withContext
                        }
                        RegistrationResult.DNI_EXISTS -> {
                            showCustomSnackbar(this@RegisterClientThreeActivity, registrationResult.messageResId, SnackbarType.ERROR)
                            return@withContext
                        }

                        RegistrationResult.DB_ERROR -> {
                            showCustomSnackbar(this@RegisterClientThreeActivity, registrationResult.messageResId, SnackbarType.ERROR)
                            return@withContext
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
}