package com.example.gymapp.data.payments

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_ACTIVITIES_PAYMENTS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_PAYMENT_ACTIVITY_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_PAYMENT_CLIENT_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_PAYMENT_DATE
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_ACTIVITY_PAYMENT_METHOD_ID

import com.example.gymapp.data.DatabaseHelper.Companion.TABLE_MEMBERSHIPS_PAYMENTS
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PAYMENT_MEMBERSHIP_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PAYMENT_METHOD_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PAYMENT_CLIENT_ID
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PAYMENT_AMOUNT
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PAYMENT_DATE
import com.example.gymapp.data.DatabaseHelper.Companion.COLUMN_MEMBERSHIP_PAYMENT_DUE_DATE

import com.example.gymapp.utils.ActionResult

class DaoPayment(private val dbWrite: SQLiteDatabase, private val dbRead: SQLiteDatabase) {
    /**
     * Registers a new membership payment in the database.
     *
     * @param membershipId The ID of the membership plan being paid.
     * @param paymentMethodId The ID of the payment method used.
     * @param clientId The ID of the client (member) making the payment.
     * @param amount The total amount of the payment.
     * @param paymentDate The date of the actual transaction (formatted as "yyyy-MM-dd").
     * @param dueDate The due date of the next payment cycle (formatted as "yyyy-MM-dd").
     * @return An [ActionResult] indicating the result of the operation.
     */
    fun registerMemberPayment(membershipId: Int, paymentMethodId: Int, clientId: Int, amount: Int, paymentDate: String, dueDate: String) : ActionResult {
        val newMemberPayment = ContentValues().apply {
            put(COLUMN_MEMBERSHIP_PAYMENT_MEMBERSHIP_ID, membershipId)
            put(COLUMN_MEMBERSHIP_PAYMENT_METHOD_ID, paymentMethodId)
            put(COLUMN_MEMBERSHIP_PAYMENT_CLIENT_ID, clientId)
            put(COLUMN_MEMBERSHIP_PAYMENT_AMOUNT, amount)
            put(COLUMN_MEMBERSHIP_PAYMENT_DATE, paymentDate)
            put(COLUMN_MEMBERSHIP_PAYMENT_DUE_DATE, dueDate)
        }
        return try {
            val newRowId = dbWrite.insert(TABLE_MEMBERSHIPS_PAYMENTS, null, newMemberPayment)
            if (newRowId != -1L) ActionResult.SUCCESS else ActionResult.ERROR
        } catch (e: Exception) {
            ActionResult.ERROR
        }
    }

    /**
     * Registers a new activity payment in the database.
     *
     * @param activityId The ID of the activity being paid for.
     * @param paymentMethodId The ID of the payment method used.
     * @param clientId The ID of the client making the payment.
     * @param paymentDate The date of the actual transaction (formatted as "yyyy-MM-dd").
     * @return An [ActionResult] indicating the result of the operation.
     */
    fun registerActivityPayment(activityId: Int, paymentMethodId: Int, clientId: Int, paymentDate: String): ActionResult {
        val newActivityPayment = ContentValues().apply {
            put(COLUMN_ACTIVITY_PAYMENT_ACTIVITY_ID, activityId)
            put(COLUMN_ACTIVITY_PAYMENT_METHOD_ID, paymentMethodId)
            put(COLUMN_ACTIVITY_PAYMENT_CLIENT_ID, clientId)
            put(COLUMN_ACTIVITY_PAYMENT_DATE, paymentDate)
        }
        return try {
            val newRowId = dbWrite.insert(TABLE_ACTIVITIES_PAYMENTS, null, newActivityPayment)
            if (newRowId != -1L) ActionResult.SUCCESS else ActionResult.ERROR
        } catch (e: Exception) {
            ActionResult.ERROR
        }
    }
}