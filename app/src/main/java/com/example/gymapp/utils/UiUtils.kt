package com.example.gymapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.gymapp.R
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat

enum class SnackbarType {
    SUCCESS, ERROR
}

fun showCustomSnackbar(context: Context, messageResId: Int, type: SnackbarType) {
    val activity = context as? Activity ?: return
    val parentLayout = activity.findViewById<View>(android.R.id.content)

    val snackbar = Snackbar.make(parentLayout, "", Snackbar.LENGTH_LONG)

    val customView = LayoutInflater.from(context).inflate(R.layout.snackbar_layout, null)

    val container = customView.findViewById<View>(R.id.snackbar_container)
    val icon = customView.findViewById<ImageView>(R.id.snackbar_icon)
    val text = customView.findViewById<TextView>(R.id.snackbar_text)

    text.text = context.getString(messageResId)

    when (type) {
        SnackbarType.SUCCESS -> {
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.color_success))
            icon.setImageResource(R.drawable.ic_message_success)
        }
        SnackbarType.ERROR -> {
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.color_error))
            icon.setImageResource(R.drawable.ic_message_error)
        }
    }

    val snackbarLayout = snackbar.view as ViewGroup
    snackbarLayout.setPadding(0, 0, 0, 0)
    snackbarLayout.addView(customView, 0)

    snackbar.show()
}

/**
 * Auxiliary function to handle the extraction and display of messages from an Intent.
 * @param context The context of the calling activity
 * @param intent The Intent that can contain the message.
 * @param messageKey The key of the extra containing the ID of the message string resource.
 */
fun handleIncomingMessage(context: Context, intent: Intent, messageKey: String) {
    if (intent.hasExtra(messageKey)) {
        val messageResId = intent.getIntExtra(messageKey, 0)
        if (messageResId != 0) {
            showCustomSnackbar(context, messageResId, SnackbarType.SUCCESS)
        }
        intent.removeExtra(messageKey)
    }
}