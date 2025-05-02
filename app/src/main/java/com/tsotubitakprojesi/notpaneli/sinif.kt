package com.tsotubitakprojesi.notpaneli

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Sinif(

    var Id: String? = null,

    @get:PropertyName("Adi")
    @set:PropertyName("Adi")
    var Adi: String = "",

    @get:PropertyName("OlusturulmaTarihi")
    @set:PropertyName("OlusturulmaTarihi")
    var OlusturulmaTarihi: Date = Date()
)
