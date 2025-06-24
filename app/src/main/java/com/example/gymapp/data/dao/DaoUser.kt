package com.example.gymapp.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.utils.ActionResult

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_USERS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_USER_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_USER_EMAIL
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_USER_PASSWORD

class DaoUser(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {

    /**
     * Register a new user if the email does not exist.
     * @param email The email of the new user.
     * @param hashedPassword The hashed password of the new user.
     * @return A success or error message.
     */
    fun registerUser(email: String, hashedPassword: String): ActionResult {
        if (checkUserEmail(email)) {
            return ActionResult.DATA_EXISTS
        }

        val values = ContentValues().apply {
            put(COLUMN_USER_EMAIL, email)
            put(COLUMN_USER_PASSWORD, hashedPassword)
        }
        val newRowId = dbWrite.insert(TABLE_USERS, null, values)
        return if (newRowId != -1L) {
            ActionResult.SUCCESS
        } else {
            ActionResult.ERROR
        }
    }

    /**
     * Verify if the email and password are correct.
     * @param email The email entered by the user.
     * @param hashedPassword The hash of the password entered by the user.
     * @return null if the credentials are incorrect (to show a Toast), or the user ID if they are correct.
     */
    fun checkLogin(email: String, hashedPassword: String): Boolean {
        val cursor = dbRead.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USER_PASSWORD),
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val storedHash = it.getString(it.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
                if (hashedPassword == storedHash) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkUserEmail(email: String): Boolean {
        val cursor = dbRead.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}