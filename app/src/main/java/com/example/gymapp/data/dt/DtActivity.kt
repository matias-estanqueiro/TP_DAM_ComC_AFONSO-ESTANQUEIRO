package com.example.gymapp.data.dt

data class DtActivity(
    val id: Int? = null,
    var name: String,
    var price: Int
) {
    init {
        this.name = name.uppercase()
    }
}