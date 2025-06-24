package com.example.gymapp.data.payments

import android.os.Parcel
import android.os.Parcelable
import com.example.gymapp.data.dt.DtClient
import java.util.Date

data class DtPaymentMembership(
    // inheritance
    override val client: DtClient,
    override val methodPaymentId: Int,
    override val date: Date,

    // DB
    val membershipId: Int,
    val dueDate: Date,

    // Voucher
    val membershipName: String,
    val amount: Int,
    val methodPaymentName: String,
    val installments: Int,

    ) : DtPayment {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(DtClient::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readSerializable() as Date,
        parcel.readInt(),
        parcel.readSerializable() as Date,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(client, flags)
        parcel.writeInt(methodPaymentId)
        parcel.writeSerializable(date)
        parcel.writeInt(membershipId)
        parcel.writeSerializable(dueDate)
        parcel.writeString(membershipName)
        parcel.writeInt(amount)
        parcel.writeString(methodPaymentName)
        parcel.writeInt(installments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DtPaymentMembership> {
        override fun createFromParcel(parcel: Parcel): DtPaymentMembership {
            return DtPaymentMembership(parcel)
        }

        override fun newArray(size: Int): Array<DtPaymentMembership?> {
            return arrayOfNulls(size)
        }
    }
}