package com.example.gymapp.data.payments

import android.os.Parcelable
import com.example.gymapp.data.dt.DtClient
import java.util.Date

/**
 * Common interface for the payment data to be sent to the voucher.
 * Both DtPaymentMembership and DtPaymentActivity will implement this interface.
 */
interface DtPayment : Parcelable {
    val client: DtClient
    val methodPaymentId: Int
    val date: Date
}