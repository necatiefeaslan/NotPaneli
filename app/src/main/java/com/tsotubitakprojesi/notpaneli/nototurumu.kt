package com.tsotubitakprojesi.notpaneli

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class NotOturumu(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var Id: String = "",

    @get:PropertyName("DersId") @set:PropertyName("DersId")
    var DersId: String = "",

    @get:PropertyName("OgretmenId") @set:PropertyName("OgretmenId")
    var OgretmenId: String = "",

    @get:PropertyName("NotTipiId") @set:PropertyName("NotTipiId")
    var NotTipiId: String = "",

    @get:PropertyName("OlusturulmaTarihi") @set:PropertyName("OlusturulmaTarihi")
    var OlusturulmaTarihi: Timestamp? = null
) 