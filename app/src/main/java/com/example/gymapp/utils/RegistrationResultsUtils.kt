package com.example.gymapp.utils

import androidx.annotation.StringRes
import com.example.gymapp.R

enum class RegistrationResult(@StringRes val messageResId: Int) {
    SUCCESS(R.string.APP_register_success),
    EMAIL_EXISTS(R.string.APP_register_email_validation),
    DB_ERROR(R.string.APP_register_error)
}
