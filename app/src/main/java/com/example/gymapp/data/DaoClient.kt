package com.example.gymapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.utils.RegistrationResult

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_CLIENTS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_DNI
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_NAME
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_SURNAME
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_STREET
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_NUMBER
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_DISTRICT
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_PHONE
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_EMAIL
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_CLIENT_TYPE


class DaoClient(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {

    /**
     * Register a new customer in the database, including uniqueness validations
     * for email and DNI.
     *
     * @param client The [DtClient] object containing the data of the client to register.
     * Must contain values for DNI and email, although other properties may be null.
     * if they are optional in your database schema.
     *
     * A [RegistrationResult] that indicates the result of the operation
     */
    fun registerClient(client: DtClient): RegistrationResult {
        if (client.email != null && checkClientEmail(client.email!!)) {
            return RegistrationResult.EMAIL_EXISTS
        }
        if (client.dni != null && checkClientDNI(client.dni!!)) {
            return RegistrationResult.DNI_EXISTS
        }

        val values = ContentValues().apply {
            put(COLUMN_CLIENT_DNI, client.dni)
            put(COLUMN_CLIENT_NAME, client.name)
            put(COLUMN_CLIENT_SURNAME, client.surname)
            put(COLUMN_CLIENT_STREET, client.street)
            put(COLUMN_CLIENT_NUMBER, client.streetNumber)
            put(COLUMN_CLIENT_DISTRICT, client.district)
            put(COLUMN_CLIENT_PHONE, client.phone)
            put(COLUMN_CLIENT_EMAIL, client.email)
            put(COLUMN_CLIENT_TYPE, client.type)
        }

        return try {
            val result = dbWrite.insert(TABLE_CLIENTS, null, values)
            if (result != -1L) RegistrationResult.SUCCESS else RegistrationResult.DB_ERROR
        } catch (e: Exception) {
            e.printStackTrace()
            RegistrationResult.DB_ERROR
        }
    }

    /**
     * Verify if an email is already registered in the database.
     * @param email The email to verify.
     * @return true if the email already exists, false otherwise.
     */
    private fun checkClientEmail(email: String): Boolean {
        val cursor = dbRead.query(
            TABLE_CLIENTS,
            arrayOf(COLUMN_CLIENT_EMAIL),
            "$COLUMN_CLIENT_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * Verify if a DNI is already registered in the database.
     * @param dni The DNI to verify.
     * @return true if the DNI already exists, false otherwise.
     */
    private fun checkClientDNI(dni: String): Boolean {
        val cursor = dbRead.query(
            TABLE_CLIENTS,
            arrayOf(COLUMN_CLIENT_DNI),
            "$COLUMN_CLIENT_DNI = ?",
            arrayOf(dni),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


}