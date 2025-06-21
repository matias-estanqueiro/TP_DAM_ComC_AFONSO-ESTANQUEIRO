package com.example.gymapp.data

import android.os.Parcel
import android.os.Parcelable

data class DtClient (
    var dni: String? = null, var name: String? = null, var surname: String? = null, var street: String? = null,
    var streetNumber: String? = null, var district: String? = null, var phone: String? = null,
    var email: String? = null, var type: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dni)
        parcel.writeString(name)
        parcel.writeString(surname)
        parcel.writeString(street)
        parcel.writeString(streetNumber)
        parcel.writeString(district)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DtClient> {
        override fun createFromParcel(parcel: Parcel): DtClient {
            return DtClient(parcel)
        }

        override fun newArray(size: Int): Array<DtClient?> {
            return arrayOfNulls(size)
        }
    }
}