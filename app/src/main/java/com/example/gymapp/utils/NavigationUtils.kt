package com.example.gymapp.utils

import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Extension function for AppCompatActivity that sets up a button
 * to navigate to the specified destination Activity.
 *
 * @param button The button to which the navigation will be assigned.
 * @param destination The class of the Activity to navigate to.
 */
fun <T> AppCompatActivity.setupNavigation(button: Button, destination: Class<T>) {
    button.setOnClickListener {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}