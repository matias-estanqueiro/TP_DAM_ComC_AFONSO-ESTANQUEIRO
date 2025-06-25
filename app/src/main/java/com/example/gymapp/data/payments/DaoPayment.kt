package com.example.gymapp.data.payments

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.gymapp.data.dt.DtPendingPayment

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

import com.example.gymapp.data.dao.DaoClient
import com.example.gymapp.utils.ActionResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    /**
     * Retrieves a list of members with pending membership payments based on a simplified logic.
     * A client is considered to have a pending payment if:
     * 1. They have no membership payments registered in the database for their client ID.
     * 2. OR, the due date of their latest payment is in a month that is two or more months
     * prior to the current month (e.g., if current month is June, and due date is April or earlier).
     *
     * @return A list of [DtPendingPayment] objects representing members with pending payments.
     */
    fun getPendingPayments(): List<DtPendingPayment> {
        val pendingPaymentsList = mutableListOf<DtPendingPayment>()
        val currentCalendar = Calendar.getInstance(Locale.getDefault())
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH) // Mes actual (0-11)

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        val daoClient = DaoClient(dbRead, dbWrite)
        val members = daoClient.getAllMembers()

        members.forEach { client ->
            val paymentsCursor = dbRead.query(
                TABLE_MEMBERSHIPS_PAYMENTS,
                arrayOf(COLUMN_MEMBERSHIP_PAYMENT_DUE_DATE, COLUMN_MEMBERSHIP_PAYMENT_DATE),
                "$COLUMN_MEMBERSHIP_PAYMENT_CLIENT_ID = ?",
                arrayOf(client.id.toString()),
                null, null,
                "$COLUMN_MEMBERSHIP_PAYMENT_DATE DESC",
                "1"
            )

            var isPending = false
            var pendingMonthString: String? = null

            paymentsCursor.use { cursor ->
                if (!cursor.moveToFirst()) {
                    isPending = true
                    pendingMonthString = "${currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} $currentYear"
                } else {
                    val dueDateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEMBERSHIP_PAYMENT_DUE_DATE))
                    val lastDueDate: Date? = try {
                        dateFormat.parse(dueDateString)
                    } catch (e: Exception) {
                        null
                    }

                    if (lastDueDate != null) {
                        val dueCalendar = Calendar.getInstance(Locale.getDefault()).apply { time = lastDueDate }
                        val dueYear = dueCalendar.get(Calendar.YEAR)
                        val dueMonth = dueCalendar.get(Calendar.MONTH)

                        val twoMonthsAgoCalendar = Calendar.getInstance(Locale.getDefault()).apply {
                            set(Calendar.YEAR, currentYear)
                            set(Calendar.MONTH, currentMonth)
                            set(Calendar.DAY_OF_MONTH, 1)
                            add(Calendar.MONTH, -2)
                        }

                        if (dueYear < twoMonthsAgoCalendar.get(Calendar.YEAR) ||
                            (dueYear == twoMonthsAgoCalendar.get(Calendar.YEAR) && dueMonth <= twoMonthsAgoCalendar.get(Calendar.MONTH))
                        ) {
                            isPending = true
                            pendingMonthString = "${currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} $currentYear"
                        }
                    }
                }
            }

            if (isPending) {
                pendingPaymentsList.add(DtPendingPayment(client, pendingMonthString ?: "Mes desconocido"))
            }
        }
        return pendingPaymentsList
    }
}