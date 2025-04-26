package com.example.tutoradmin.model

import android.os.Parcel
import android.os.Parcelable

data class Pendingdetails(
    val userId: String? = null,
    val username: String? = null,
    val dates: String? = null,
    val times: String? = null,
    val standard: String? =null,
    val subnames: List<String>? = null,
    val teacherNames: List<String>? = null,
    val teacherImages: List<String>? = null,
    val subPrices: List<String>? = null,
    val phoneNumber: String? = null,
    val disabilities: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val currentTime: Long? = null,
    val itemPushKey: String? = null,
    val Rejected: Boolean? = null,
    val isCompleted: Boolean? = null,
    val orderAccepted: Boolean = false,
    val reachteacher:Boolean = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(dates)
        parcel.writeString(times)
        parcel.writeString(standard)
        parcel.writeStringList(subnames)
        parcel.writeStringList(teacherNames)
        parcel.writeStringList(teacherImages)
        parcel.writeStringList(subPrices)
        parcel.writeString(phoneNumber)
        parcel.writeString(disabilities)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeValue(currentTime)
        parcel.writeString(itemPushKey)
        parcel.writeValue(Rejected)
        parcel.writeValue(isCompleted)
        parcel.writeByte(if (reachteacher) 1 else 0)
        parcel.writeByte(if (orderAccepted) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pendingdetails> {
        override fun createFromParcel(parcel: Parcel): Pendingdetails {
            return Pendingdetails(parcel)
        }

        override fun newArray(size: Int): Array<Pendingdetails?> {
            return arrayOfNulls(size)
        }
    }
}
