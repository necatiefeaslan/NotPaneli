package com.tsotubitakprojesi.notpaneli

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore


class SinavActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var kaydetButton: Button
    private lateinit var switchTumunuYuzYap: Switch
    private lateinit var buttonGeri: ImageButton
    private lateinit var adapter: SinavOgrenciAdapter
    private val db = FirebaseFirestore.getInstance()
    private var ogrenciler: List<Ogrenci> = listOf()
    private var dersId: String? = null
    private var ogretmen: Ogretmen? = null
    private var notTipi: NotTipi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sinav)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewOgrenciler)
        kaydetButton = findViewById(R.id.buttonKaydet)
        switchTumunuYuzYap = findViewById(R.id.switchTumunuYuzYap)
        buttonGeri = findViewById(R.id.buttonGeri)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonGeri.setOnClickListener { finish() }

        dersId = intent.getStringExtra("dersId")
        ogretmen = intent.getParcelableExtra("ogretmen")
        notTipi = intent.getParcelableExtra("notTipi")

        if (dersId == null || ogretmen == null || notTipi == null) {
            Toast.makeText(this, "Eksik bilgi!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ogrencileriGetirVeListele()

        switchTumunuYuzYap.isChecked = false

        kaydetButton.setOnClickListener {
            puanlariKaydet()
        }

        switchTumunuYuzYap.setOnCheckedChangeListener { _, isChecked ->
            if (::adapter.isInitialized) {
                if (isChecked) {
                    adapter.tumPuanlariAyarla(100)
                } else {
                    adapter.tumPuanlariAyarla(0)
                }
            }
        }
    }

    private fun ogrencileriGetirVeListele() {
        db.collection("Ders").document(dersId!!).get().addOnSuccessListener { dersDoc ->
            val sinifId = dersDoc.getString("SinifId")
            if (sinifId == null) {
                Toast.makeText(this, "Sınıf bulunamadı!", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            db.collection("Ogrenci").whereEqualTo("SinifId", sinifId).get().addOnSuccessListener { ogrenciDocs ->
                ogrenciler = ogrenciDocs.map {
                    Ogrenci(
                        Id = it.id,
                        Adi = it.getString("Adi") ?: "",
                        SinifId = it.getString("SinifId") ?: "",
                        OkulNo = it.getString("OkulNo"),
                        Email = it.getString("Email"),
                        Telefon = it.getString("Telefon")
                    )
                }
                adapter = SinavOgrenciAdapter(ogrenciler, this) { ogrenciId, puan ->
                    // Puan değiştiğinde yapılacak işlemler
                }
                recyclerView.adapter = adapter
            }
        }
    }

    private fun puanlariKaydet() {
        val puanListesi = adapter.getPuanlar()
        val oturumRef = db.collection("NotOturumu").document()
        val notOturumu = hashMapOf(
            "Id" to oturumRef.id,
            "DersId" to dersId,
            "OgretmenId" to ogretmen!!.Id,
            "NotTipiId" to notTipi!!.Id,
            "OlusturulmaTarihi" to Timestamp.now()
        )
        oturumRef.set(notOturumu).addOnSuccessListener {
            val batch = db.batch()
            for ((ogrenciId, puan) in puanListesi) {
                val notRef = db.collection("Not").document()
                val not = hashMapOf(
                    "OgrenciId" to ogrenciId,
                    "NotOturumuId" to oturumRef.id,
                    "Puan" to puan,
                    "OlusturulmaTarihi" to Timestamp.now()
                )
                batch.set(notRef, not)
            }
            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Sınav notları kaydedildi!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Sınav notları kaydedilemedi!", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 