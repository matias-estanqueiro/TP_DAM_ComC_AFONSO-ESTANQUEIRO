package com.example.gymapp.utils

import androidx.annotation.StringRes
import com.example.gymapp.R

enum class ActionResult(@StringRes val messageResId: Int) {
    SUCCESS(R.string.APP_success),
    ERROR(R.string.APP_error),
    NOT_FOUND(R.string.APP_not_found),
    DATA_EXISTS(R.string.APP_data_exists),
}
