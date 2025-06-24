package com.example.gymapp.data.dao

import android.database.sqlite.SQLiteDatabase

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_PAYMENTS_METHODS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_PAYMENT_METHOD_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_PAYMENT_METHOD_DESCRIPTION
import com.example.gymapp.data.dt.DtPaymentType

class DaoPaymentType(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {

    /**
     * Retrieves all payment types registered in the database.
     *
     * @return A list of [DtPaymentType] objects.
     * Returns an empty list if there are no payment types or if an error occurs.
     */
    fun getAllPaymentTypes(): List<DtPaymentType> {
        val paymentTypes = mutableListOf<DtPaymentType>()
        val cursor = dbRead.query(
            TABLE_PAYMENTS_METHODS,
            arrayOf(COLUMN_PAYMENT_METHOD_ID, COLUMN_PAYMENT_METHOD_DESCRIPTION),
            null, null, null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD_DESCRIPTION))
                paymentTypes.add(DtPaymentType(id, name))
            }
        }
        return paymentTypes
    }
}