package com.tsotubitakprojesi.notpaneli

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Not(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var Id: String = "",

    @get:PropertyName("OgrenciId") @set:PropertyName("OgrenciId")
    var OgrenciId: String = "",

    @get:PropertyName("NotOturumuId") @set:PropertyName("NotOturumuId")
    var NotOturumuId: String = "",

    @get:PropertyName("Puan") @set:PropertyName("Puan")
    var Puan: Int = 0,

    @get:PropertyName("Yorum") @set:PropertyName("Yorum")
    var Yorum: String? = null,

    @get:PropertyName("OlusturulmaTarihi") @set:PropertyName("OlusturulmaTarihi")
    var OlusturulmaTarihi: Timestamp? = null,

    @get:PropertyName("GuncellenmeTarihi") @set:PropertyName("GuncellenmeTarihi")
    var GuncellenmeTarihi: Timestamp? = null,

    @get:PropertyName("GuncelleyenId") @set:PropertyName("GuncelleyenId")
    var GuncelleyenId: String? = null
) 