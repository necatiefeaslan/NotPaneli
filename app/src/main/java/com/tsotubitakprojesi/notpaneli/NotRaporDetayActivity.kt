package com.tsotubitakprojesi.notpaneli

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageButton

class NotRaporDetayActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewTarih: TextView
    private val db = FirebaseFirestore.getInstance()
    private var notTipiId: String? = null
    private var dersId: String? = null
    private var ogretmenId: String? = null
    private var secilenTarih: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_rapor_detay)

        recyclerView = findViewById(R.id.recyclerViewDetay)
        textViewTarih = findViewById(R.id.textViewDetayTarih)
        recyclerView.layoutManager = LinearLayoutManager(this)

        notTipiId = intent.getStringExtra("notTipiId")
        dersId = intent.getStringExtra("dersId")
        ogretmenId = intent.getStringExtra("ogretmenId")
        secilenTarih = intent.getStringExtra("tarih")

        textViewTarih.text = secilenTarih ?: "Tarih"

        if (notTipiId != null && dersId != null && ogretmenId != null && secilenTarih != null) {
            db.collection("NotOturumu")
                .whereEqualTo("DersId", dersId)
                .whereEqualTo("OgretmenId", ogretmenId)
                .whereEqualTo("NotTipiId", notTipiId)
                .get()
                .addOnSuccessListener { oturumlar ->
                    val oturumIdler = oturumlar.documents.map { it.id }
                    if (oturumIdler.isEmpty()) {
                        recyclerView.adapter = NotRaporDetayAdapter(emptyList())
                        return@addOnSuccessListener
                    }
                    db.collection("Not")
                        .whereIn("NotOturumuId", oturumIdler)
                        .get()
                        .addOnSuccessListener { notlarDocs ->
                            val ogrenciIdSet = notlarDocs.mapNotNull { it.getString("OgrenciId") }.toSet()
                            db.collection("Ogrenci").whereIn("Id", ogrenciIdSet.toList()).get().addOnSuccessListener { ogrenciDocs ->
                                val ogrenciAdMap = ogrenciDocs.associateBy({ it.id }, { it.getString("Adi") ?: "" })
                                val ogrenciNotMap = mutableMapOf<String, MutableList<Int>>()
                                for (notDoc in notlarDocs) {
                                    val ogrenciId = notDoc.getString("OgrenciId") ?: continue
                                    val puan = notDoc.getLong("Puan")?.toInt() ?: 0
                                    val tarih = notDoc.getTimestamp("OlusturulmaTarihi")?.toDate()
                                    val tarihStr = tarih?.let { java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(it) } ?: ""
                                    if (tarihStr == secilenTarih) {
                                        val ogrenciAdi = ogrenciAdMap[ogrenciId] ?: ""
                                        if (ogrenciAdi.isNotEmpty()) {
                                            ogrenciNotMap.getOrPut(ogrenciAdi) { mutableListOf() }.add(puan)
                                        }
                                    }
                                }
                                val notlar = ogrenciNotMap.map { (ogrenciAdi, puanlar) ->
                                    NotRaporDetayItem(ogrenciAdi, puanlar)
                                }
                                recyclerView.adapter = NotRaporDetayAdapter(notlar)
                            }
                        }
                }
        }

        findViewById<ImageButton>(R.id.buttonDetayGeri).setOnClickListener { finish() }
    }
}

data class NotRaporDetayItem(
    val ogrenciAdi: String,
    val puanlar: List<Int>
) 