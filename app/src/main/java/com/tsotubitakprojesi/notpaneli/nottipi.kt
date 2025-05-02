package com.tsotubitakprojesi.notpaneli

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

import android.os.Parcel
import android.os.Parcelable

data class NotTipi(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var Id: String = "",

    @get:PropertyName("Adi") @set:PropertyName("Adi")
    var Adi: String = "",

    @get:PropertyName("Aciklama") @set:PropertyName("Aciklama")
    var Aciklama: String? = null,

    @get:PropertyName("OlusturulmaTarihi") @set:PropertyName("OlusturulmaTarihi")
    var OlusturulmaTarihi: Timestamp? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Id)
        parcel.writeString(Adi)
        parcel.writeString(Aciklama)
        parcel.writeParcelable(OlusturulmaTarihi, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotTipi> {
        override fun createFromParcel(parcel: Parcel): NotTipi {
            return NotTipi(parcel)
        }

        override fun newArray(size: Int): Array<NotTipi?> {
            return arrayOfNulls(size)
        }
    }
}