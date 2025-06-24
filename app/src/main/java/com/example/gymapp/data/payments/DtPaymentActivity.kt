package com.example.gymapp.data.payments

import android.os.Parcel
import android.os.Parcelable
import com.example.gymapp.data.dt.DtClient
import java.util.Date

data class DtPaymentActivity(
    // inheritance
    override val client: DtClient,
    override val methodPaymentId: Int,
    override val date: Date,

    // DB
    val activityId: Int,

    // Voucher
    val activityName: String,
    val activityPrice: Int,
) : DtPayment {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(DtClient::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readSerializable() as Date,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(client, flags)
        parcel.writeInt(methodPaymentId)
        parcel.writeSerializable(date)
        parcel.writeInt(activityId)
        parcel.writeString(activityName)
        parcel.writeInt(activityPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DtPaymentActivity> {
        override fun createFromParcel(parcel: Parcel): DtPaymentActivity {
            return DtPaymentActivity(parcel)
        }

        override fun newArray(size: Int): Array<DtPaymentActivity?> {
            return arrayOfNulls(size)
        }
    }
}