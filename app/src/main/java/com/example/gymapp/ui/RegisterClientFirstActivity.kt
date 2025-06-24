package com.example.gymapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.CheckBox
import android.widget.LinearLayout
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.gymapp.R
import com.example.gymapp.data.dt.DtClient
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.handleIncomingMessage
import com.example.gymapp.utils.isValidDNI
import com.example.gymapp.utils.isValidLettersAndSpaces
import com.example.gymapp.utils.showCustomSnackbar

class RegisterClientFirstActivity : AppCompatActivity() {
    private var currentClient: DtClient = DtClient()

    private lateinit var dniInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var checkFitnessCertificate: CheckBox
    private lateinit var formRegisterClient: LinearLayout
    private lateinit var btnNext: Button

    companion object {
        const val CLIENT_DATA = "client_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_client_first)

        dniInput = findViewById(R.id.inpClientDni)
        nameInput = findViewById(R.id.inpClientName)
        surnameInput = findViewById(R.id.inpClientSurname)
        checkFitnessCertificate = findViewById(R.id.chkFitnessCertificate)
        formRegisterClient = findViewById(R.id.frmClientRegister)
        btnNext = findViewById(R.id.btnNext)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_client_first)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val incomingIntent = intent
        handleIncomingMessage(this, incomingIntent,  RegisterClientThreeActivity.REGISTER_SUCCESS_MESSAGE)

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
        updateFormVisibilityAndButtonState(checkFitnessCertificate.isChecked)

        checkFitnessCertificate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                formRegisterClient.visibility = View.VISIBLE
                btnNext.isEnabled = true

            } else {
                formRegisterClient.visibility = View.GONE
                btnNext.isEnabled = false
            }
        }

        btnNext.setOnClickListener {
            val clientDNI = dniInput.text.toString().trim()
            val clientName = nameInput.text.toString().trim()
            val clientSurname = surnameInput.text.toString().trim()

            // Validations
            // 1. All inputs + check are required
            // 2. DNI format 8 digits
            // 3. Name and surname only letters and spaces
            if(clientDNI.isEmpty() || clientName.isEmpty() || clientSurname.isEmpty()) {
                showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if(!isValidDNI(clientDNI)) {
                showCustomSnackbar(this, R.string.CLIENT_dni_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if(!isValidLettersAndSpaces(clientName) || !isValidLettersAndSpaces(clientSurname)) {
                showCustomSnackbar(this, R.string.CLIENT_name_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            currentClient.dni = clientDNI
            currentClient.name = clientName
            currentClient.surname = clientSurname

            val intent = Intent(this, RegisterClientTwoActivity::class.java).apply {
                putExtra(CLIENT_DATA, currentClient)
            }
            startActivity(intent)
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
            updateFormVisibilityAndButtonState(checkFitnessCertificate.isChecked)
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
    private fun updateUIWithClientData(client: DtClient) {
        dniInput.setText(client.dni)
        nameInput.setText(client.name)
        surnameInput.setText(client.surname)
        checkFitnessCertificate.isChecked = true
    }

    /**
     * Auxiliary function to update the form visibility.
     */
    private fun updateFormVisibilityAndButtonState(isChecked: Boolean) {
        if (isChecked) {
            formRegisterClient.visibility = View.VISIBLE
            btnNext.isEnabled = true
        } else {
            formRegisterClient.visibility = View.GONE
            btnNext.isEnabled = false
        }
    }
}