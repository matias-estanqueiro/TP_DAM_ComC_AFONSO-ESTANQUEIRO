package com.example.gymapp.utils

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidLettersAndSpaces(text: String): Boolean {
    return text.matches("^[a-zA-Z ]+$".toRegex())
}

fun isValidDNI(text: String): Boolean {
    return text.matches("^\\d{8}$".toRegex())
}

fun isValidOnlyNumbers(text: String): Boolean {
    return text.matches("^\\d+$".toRegex())
}