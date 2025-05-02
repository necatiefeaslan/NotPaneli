package com.tsotubitakprojesi.notpaneli

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

import android.os.Parcel
import android.os.Parcelable

data class Ogretmen(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var Id: String = "",

    @get:PropertyName("Email") @set:PropertyName("Email")
    var Email: String = "",

    @get:PropertyName("Telefon") @set:PropertyName("Telefon")
    var Telefon: String? = null,

    @get:PropertyName("Adi") @set:PropertyName("Adi")
    var Adi: String = "",

    @get:PropertyName("OlusturulmaTarihi") @set:PropertyName("OlusturulmaTarihi")
    var OlusturulmaTarihi: Timestamp? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Id)
        parcel.writeString(Email)
        parcel.writeString(Telefon)
        parcel.writeString(Adi)
        parcel.writeParcelable(OlusturulmaTarihi, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ogretmen> {
        override fun createFromParcel(parcel: Parcel): Ogretmen {
            return Ogretmen(parcel)
        }

        override fun newArray(size: Int): Array<Ogretmen?> {
            return arrayOfNulls(size)
        }
    }
}