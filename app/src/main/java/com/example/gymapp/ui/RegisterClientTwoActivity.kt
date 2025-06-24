package com.example.gymapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.R
import com.example.gymapp.data.dt.DtClient
import com.example.gymapp.utils.showCustomSnackbar
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.isValidOnlyNumbers
import com.example.gymapp.ui.RegisterClientFirstActivity.Companion.CLIENT_DATA
import com.example.gymapp.utils.isValidLettersAndSpaces

class RegisterClientTwoActivity : AppCompatActivity() {
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button
    private lateinit var inpClientStreet: EditText
    private lateinit var inpClientStreetNumber: EditText
    private lateinit var inpClientDistrict: EditText

    private var currentClient: DtClient = DtClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_client_two)

        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)
        inpClientStreet = findViewById(R.id.inpClientStreet)
        inpClientStreetNumber = findViewById(R.id.inpClientNumber)
        inpClientDistrict = findViewById(R.id.inpClientDistrict)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_client_two)) { v, insets ->
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
            currentClient.street = inpClientStreet.text.toString().trim()
            currentClient.streetNumber = inpClientStreetNumber.text.toString().trim()
            currentClient.district = inpClientDistrict.text.toString().trim()

            val backIntent = Intent(this, RegisterClientFirstActivity::class.java).apply {
                putExtra(CLIENT_DATA, currentClient)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(backIntent)
            finish()
        }

        btnNext.setOnClickListener {
            val clientStreet = inpClientStreet.text.toString().trim()
            val clientStreetNumber = inpClientStreetNumber.text.toString().trim()
            val clientDistrict = inpClientDistrict.text.toString().trim()

            // Validations
            // 1. All fields are required
            // 2. Street Number can only contain number
            // 3. District can only contain letters and spaces
            if (clientStreet.isEmpty() || clientStreetNumber.isEmpty() || clientDistrict.isEmpty()) {
                showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if (!isValidOnlyNumbers(clientStreetNumber)) {
                showCustomSnackbar(this, R.string.CLIENT_street_number_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            if(!isValidLettersAndSpaces(clientDistrict)) {
                showCustomSnackbar(this, R.string.CLIENT_district_validation, SnackbarType.ERROR)
                return@setOnClickListener
            }

            currentClient.street = clientStreet
            currentClient.streetNumber = clientStreetNumber
            currentClient.district = clientDistrict

            val intent = Intent(this, RegisterClientThreeActivity::class.java).apply {
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
     * Auxiliary function to update the UI of this Activity with the data of the DtClient object.
     */
    private fun updateUIWithClientData(client: DtClient?) {
        client?.let {
            inpClientStreet.setText(it.street)
            inpClientStreetNumber.setText(it.streetNumber)
            inpClientDistrict.setText(it.district)
        }
    }
}