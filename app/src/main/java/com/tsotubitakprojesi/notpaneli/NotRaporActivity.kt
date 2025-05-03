package com.tsotubitakprojesi.notpaneli

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class NotRaporActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewBaslik: TextView
    private lateinit var buttonGeri: ImageButton
    private val db = FirebaseFirestore.getInstance()
    private var notTipiId: String? = null
    private var dersId: String? = null
    private var ogretmenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_rapor)

        recyclerView = findViewById(R.id.recyclerViewRapor)
        textViewBaslik = findViewById(R.id.textViewRaporBaslik)
        buttonGeri = findViewById(R.id.buttonRaporGeri)
        recyclerView.layoutManager = LinearLayoutManager(this)

        notTipiId = intent.getStringExtra("notTipiId")
        dersId = intent.getStringExtra("dersId")
        ogretmenId = intent.getStringExtra("ogretmenId")

        buttonGeri.setOnClickListener {
            finish()
        }

        // Başlık ayarla
        textViewBaslik.text = "Geçmiş Notlar"

        // Burada Firestore'dan eski notları çekip RecyclerView'a aktaracağız
        if (notTipiId != null && dersId != null && ogretmenId != null) {
            // Önce ilgili NotOturumu'nu bul
            db.collection("NotOturumu")
                .whereEqualTo("DersId", dersId)
                .whereEqualTo("OgretmenId", ogretmenId)
                .whereEqualTo("NotTipiId", notTipiId)
                .get()
                .addOnSuccessListener { oturumlar ->
                    val oturumIdler = oturumlar.documents.map { it.id }
                    if (oturumIdler.isEmpty()) {
                        recyclerView.adapter = NotRaporAdapter(emptyList()) { }
                        return@addOnSuccessListener
                    }
                    // Notları çek
                    db.collection("Not")
                        .whereIn("NotOturumuId", oturumIdler)
                        .get()
                        .addOnSuccessListener { notlarDocs ->
                            val notlar = mutableListOf<NotRaporItem>()
                            val eklenenTarihler = mutableSetOf<String>()
                            val ogrenciIdSet = notlarDocs.mapNotNull { it.getString("OgrenciId") }.toSet()
                            // Öğrenci adlarını topluca çek
                            db.collection("Ogrenci").whereIn("Id", ogrenciIdSet.toList()).get().addOnSuccessListener { ogrenciDocs ->
                                val ogrenciAdMap = ogrenciDocs.associateBy({ it.id }, { it.getString("Adi") ?: "" })
                                for (notDoc in notlarDocs) {
                                    val tarih = notDoc.getTimestamp("OlusturulmaTarihi")?.toDate()
                                    val tarihStr = tarih?.let { java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(it) } ?: ""
                                    if (tarihStr.isNotEmpty() && eklenenTarihler.add(tarihStr)) {
                                        val ogrenciAdi = ogrenciAdMap[notDoc.getString("OgrenciId")] ?: ""
                                        val puan = notDoc.getDouble("Puan")?.toInt() ?: 0
                                        notlar.add(NotRaporItem(ogrenciAdi, puan, tarihStr))
                                    }
                                }
                                recyclerView.adapter = NotRaporAdapter(notlar) { secilenTarih ->
                                    val intent = Intent(this, NotRaporDetayActivity::class.java)
                                    intent.putExtra("tarih", secilenTarih)
                                    intent.putExtra("notTipiId", notTipiId)
                                    intent.putExtra("dersId", dersId)
                                    intent.putExtra("ogretmenId", ogretmenId)
                                    startActivity(intent)
                                }
                            }

                        }
                }
        }
    }
} 