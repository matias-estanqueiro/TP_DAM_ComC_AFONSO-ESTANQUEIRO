package com.example.gymapp

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout

class HeaderLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.header_layout, this, true)

        val homeButton: ImageButton = findViewById(R.id.btnHome)
        homeButton.setOnClickListener {
            val dashboardIntent = Intent(context, DashboardActivity::class.java)
            context.startActivity(dashboardIntent)
        }

        val logOutButton: ImageButton = findViewById(R.id.btnLogOut)
        logOutButton.setOnClickListener {
            val mainIntent = Intent(context, MainActivity::class.java)
            context.startActivity(mainIntent)
        }
    }
}