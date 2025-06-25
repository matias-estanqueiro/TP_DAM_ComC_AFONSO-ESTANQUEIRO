package com.example.gymapp.ui.clients

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.R
import com.example.gymapp.data.dao.DaoClient
import com.example.gymapp.utils.ActionResult
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.isValidOnlyNumbers

import com.example.gymapp.utils.showCustomSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SearchCardIdActivity : AppCompatActivity() {
    private lateinit var inpMemberSearch: EditText
    private lateinit var frmIdCard: LinearLayout
    private lateinit var lblMemberName: TextView
    private lateinit var lblMemberNumber: TextView
    private lateinit var lblMemberPlan: TextView
    private lateinit var lblYear: TextView
    private lateinit var btnAction: Button
    private lateinit var btnClear: Button
    private lateinit var buttonSpace: View

    private lateinit var daoClient: DaoClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_card_id)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_card_id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        frmIdCard = findViewById(R.id.frmIdCard)
        inpMemberSearch = findViewById(R.id.inpMemberSearch)
        lblMemberName = findViewById(R.id.lblMemberName)
        lblMemberNumber = findViewById(R.id.lblMemberNumber)
        lblMemberPlan = findViewById(R.id.lblMemberPlan)
        lblYear = findViewById(R.id.lblYear)
        btnAction = findViewById(R.id.btnAction)
        btnClear = findViewById(R.id.btnClear)
        buttonSpace = findViewById(R.id.buttonSpace)

        daoClient = (application as FitnessSportsApp).daoClient

        resetToSearchState()

        btnAction.setOnClickListener {
            if (btnAction.text == getString(R.string.FORM_search)) {
                handleSearchAction()
            } else {
                handleSendEmailAction()
            }
        }

        btnClear.setOnClickListener {
            resetToSearchState()
        }
    }

    /**
     * Handles logic when the “Action” button is in “Search” mode.
     */
    private fun handleSearchAction() {
        val dni = inpMemberSearch.text.toString().trim()

        if (dni.isEmpty()) {
            showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
            return
        }

        if (!isValidOnlyNumbers(dni)) {
            showCustomSnackbar(this, R.string.CLIENT_dni_validation, SnackbarType.ERROR)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val memberToShow = daoClient.getMemberByDni(dni)
            withContext(Dispatchers.Main) {
                if (memberToShow == null) {
                    showCustomSnackbar(this@SearchCardIdActivity, ActionResult.NOT_FOUND.messageResId, SnackbarType.ERROR)
                } else {
                    lblMemberName.text = getString(R.string.FORMAT_full_name, memberToShow.name, memberToShow.surname)
                    lblMemberNumber.text = memberToShow.dni
                    lblMemberPlan.text = memberToShow.planName
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    lblYear.text = currentYear.toString()
                    frmIdCard.visibility = View.VISIBLE
                    setButtonsStateForFoundMember()
                }
            }
        }
    }

    /**
     * Handles logic when the “Action” button is in “Send Email” mode.
     */
    private fun handleSendEmailAction() {
        showCustomSnackbar(this, ActionResult.SUCCESS.messageResId, SnackbarType.SUCCESS)
        resetToSearchState()
    }

    /**
     * Sets the state of the buttons and the form when a member is found.
     */
    private fun setButtonsStateForFoundMember() {
        btnAction.layoutParams.width = (180 * resources.displayMetrics.density).toInt()
        btnClear.layoutParams.width = (180 * resources.displayMetrics.density).toInt()

        btnClear.visibility = View.VISIBLE
        buttonSpace.visibility = View.VISIBLE
        btnAction.setText(R.string.FORM_email)
    }

    /**
     * Resets the form and buttons to the initial search state.
     */
    private fun resetToSearchState() {
        frmIdCard.visibility = View.GONE
        inpMemberSearch.setText("")
        btnClear.visibility = View.GONE
        buttonSpace.visibility = View.GONE
        btnAction.setText(R.string.FORM_search)

        btnAction.layoutParams.width = (250 * resources.displayMetrics.density).toInt()
    }
}