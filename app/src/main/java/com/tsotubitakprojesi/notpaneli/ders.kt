package com.tsotubitakprojesi.notpaneli

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Ders(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var Id: String = "",

    @get:PropertyName("DersAdi") @set:PropertyName("DersAdi")
    var DersAdi: String = "",

    @get:PropertyName("OgretmenId") @set:PropertyName("OgretmenId")
    var OgretmenId: String = "",

    @get:PropertyName("SinifId") @set:PropertyName("SinifId")
    var SinifId: String = "",

    @get:PropertyName("EklenmeTarihi") @set:PropertyName("EklenmeTarihi")
    var EklenmeTarihi: Timestamp? = null
) 