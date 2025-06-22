package com.example.gymapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.example.gymapp.FitnessSportsApp
import com.example.gymapp.adapters.ActivityListAdapter
import com.example.gymapp.adapters.OnActivityActionClickListener
import com.example.gymapp.data.DaoActivity
import com.example.gymapp.data.DtActivity
import com.example.gymapp.utils.ActionResult
import com.example.gymapp.utils.SnackbarType
import com.example.gymapp.utils.isValidOnlyNumbers
import com.example.gymapp.utils.showCustomSnackbar

class RegisterClassActivity : AppCompatActivity(), OnActivityActionClickListener {
    private lateinit var inpClassName: EditText
    private lateinit var inpClassValue: EditText
    private lateinit var btnAction: Button
    private lateinit var listViewActivities: ListView

    private lateinit var activityListAdapter: ActivityListAdapter
    private lateinit var daoActivity: DaoActivity

    private var activitiesList: MutableList<DtActivity> = mutableListOf()
    private var activityToEdit: DtActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_class)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_class)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inpClassName = findViewById(R.id.inpClassName)
        inpClassValue = findViewById(R.id.inpClassValue)
        btnAction = findViewById(R.id.btnRegister)
        listViewActivities = findViewById(R.id.lstActivities)

        daoActivity = (application as FitnessSportsApp).daoActivity

        activityListAdapter = ActivityListAdapter(this, activitiesList, this)
        listViewActivities.adapter = activityListAdapter

        loadActivities()

        btnAction.setOnClickListener {
            if (activityToEdit == null) {
                registerNewActivity()
            } else {
                updateExistingActivity(activityToEdit!!)
                activityToEdit = null
            }
        }

        // setupNavigation(findViewById(R.id.btnRegister), DashboardActivity::class.java)
    }

    private fun registerNewActivity() {
        val name = inpClassName.text.toString().trim()
        val price = inpClassValue.text.toString().trim()

        // Validations
        // 1. All fields are required
        // 2. Price can only have numbers
        if (name.isEmpty() || price.isEmpty()) {
            showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
            return
        }

        if (!isValidOnlyNumbers((price))) {
            showCustomSnackbar(this, R.string.ACTIVITY_value_validation, SnackbarType.ERROR)
            return
        }

        val newActivity = DtActivity(name = name, price = price.toInt())

        CoroutineScope(Dispatchers.IO).launch {
            val actionResult = daoActivity.registerActivity(newActivity)
            withContext(Dispatchers.Main) {
                when (actionResult) {
                    ActionResult.SUCCESS -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.SUCCESS)
                        loadActivities()
                        clearForm()
                    }
                    ActionResult.ERROR -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                        return@withContext
                    }

                    ActionResult.DATA_EXISTS -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                        return@withContext
                    }

                    ActionResult.NOT_FOUND -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                        return@withContext
                    }
                }
            }
        }
    }

    /**
     * Updates an existing activity.
     * @param activity The activity with the updated data, including its ID.
     */
    private fun updateExistingActivity(activity: DtActivity) {
        val name = inpClassName.text.toString().trim()
        val price = inpClassValue.text.toString().trim()

        // Validations
        // 1. All fields are required
        // 2. Price can only have numbers
        if (name.isEmpty() || price.isEmpty()) {
            showCustomSnackbar(this, R.string.APP_required_validation, SnackbarType.ERROR)
            return
        }

        if (!isValidOnlyNumbers((price))) {
            showCustomSnackbar(this, R.string.ACTIVITY_value_validation, SnackbarType.ERROR)
            return
        }

        val updatedActivity = activity.copy(name = name, price = price.toInt())

        CoroutineScope(Dispatchers.IO).launch {
            val actionResult = daoActivity.updateActivity(updatedActivity)
            withContext(Dispatchers.Main) {
                when (actionResult) {
                    ActionResult.SUCCESS -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.SUCCESS)
                        loadActivities()
                        clearForm()
                        btnAction.text = getString(R.string.FORM_register)
                    }
                    ActionResult.ERROR -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                        return@withContext
                    }
                    ActionResult.DATA_EXISTS -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                        return@withContext
                    }
                    ActionResult.NOT_FOUND -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                        return@withContext
                    }
                }
            }
        }
    }

    /**
     * Loads all activities and updates the ListView.
     */
    private fun loadActivities() {
        CoroutineScope(Dispatchers.IO).launch {
            val activities = daoActivity.getAllActivities()
            withContext(Dispatchers.Main) {
                activityListAdapter.updateData(activities)
            }
        }
    }

    /**
     * Clears the form fields.
     */
    private fun clearForm() {
        inpClassName.text.clear()
        inpClassValue.text.clear()
        inpClassName.requestFocus()
    }

    // OnActivityActionClickListener interface methods ---

    /**
     * Manages the click on the edit button of a list item.
     * Fills in the form with the activity data for editing.
     */
    override fun onEditActivity(activity: DtActivity) {
        activityToEdit = activity
        inpClassName.setText(activity.name)
        inpClassValue.setText(activity.price.toString())
        btnAction.text = getString(R.string.ACTIVITY_update)
    }

    /**
     * Handles the click on the delete button of a list item.
     * Deletes the activity from the database.
     */
    override fun onDeleteActivity(activity: DtActivity) {
        activity.id?.let { activityId ->
            val actionResult = daoActivity.deleteActivity(activityId)
            CoroutineScope(Dispatchers.IO).launch {
                when (actionResult) {
                    ActionResult.SUCCESS -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.SUCCESS)
                        loadActivities()
                        clearForm()
                        btnAction.text = getString(R.string.FORM_register)
                    }
                    ActionResult.ERROR -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                    }
                    ActionResult.DATA_EXISTS -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                    }
                    ActionResult.NOT_FOUND -> {
                        showCustomSnackbar(this@RegisterClassActivity, actionResult.messageResId, SnackbarType.ERROR)
                    }
                }
            }
        }
    }
}