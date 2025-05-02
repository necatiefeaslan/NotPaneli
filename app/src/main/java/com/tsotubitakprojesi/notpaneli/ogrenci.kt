package com.tsotubitakprojesi.notpaneli

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Ogrenci(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var Id: String = "",

    @get:PropertyName("Adi") @set:PropertyName("Adi")
    var Adi: String = "",

    @get:PropertyName("SinifId") @set:PropertyName("SinifId")
    var SinifId: String = "",

    @get:PropertyName("OkulNo") @set:PropertyName("OkulNo")
    var OkulNo: String? = null,

    @get:PropertyName("Email") @set:PropertyName("Email")
    var Email: String? = null,

    @get:PropertyName("Telefon") @set:PropertyName("Telefon")
    var Telefon: String? = null,

    @get:PropertyName("OlusturulmaTarihi") @set:PropertyName("OlusturulmaTarihi")
    var OlusturulmaTarihi: Timestamp? = null
) 