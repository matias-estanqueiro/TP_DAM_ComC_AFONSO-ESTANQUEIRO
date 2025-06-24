package com.example.gymapp.data.dt

import android.os.Parcel
import android.os.Parcelable

data class DtClient (
    val id: Int? = null, var dni: String? = null, var name: String? = null, var surname: String? = null, var street: String? = null,
    var streetNumber: String? = null, var district: String? = null, var phone: String? = null,
    var email: String? = null, var type: Int = 0, var plan: Int = 0, var planName: String? = null
) : Parcelable {
    init {
        this.name = name?.uppercase()
        this.surname = surname?.uppercase()
        this.street = street?.uppercase()
        this.district = district?.uppercase()
        this.planName = planName?.uppercase()
    }
    constructor(parcel: Parcel) : this(
        if (parcel.readInt() == 1) parcel.readInt() else null,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        if (id != null) {
            parcel.writeInt(1)
            parcel.writeInt(id)
        } else {
            parcel.writeInt(0)
        }
        parcel.writeString(dni)
        parcel.writeString(name)
        parcel.writeString(surname)
        parcel.writeString(street)
        parcel.writeString(streetNumber)
        parcel.writeString(district)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeInt(type)
        parcel.writeInt(plan)
        parcel.writeString(planName)
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