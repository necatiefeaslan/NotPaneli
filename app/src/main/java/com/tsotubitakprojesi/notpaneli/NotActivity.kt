package com.tsotubitakprojesi.notpaneli

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.slider.Slider

class NotActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var kaydetButton: Button
    private lateinit var ogrenciAdapter: OgrenciPuanAdapter
    private lateinit var textViewNotBaslik: TextView
    private lateinit var buttonGeri: ImageButton
    private lateinit var sliderOrtakPuan: Slider
    private lateinit var textViewOrtakPuan: TextView
    private val db = FirebaseFirestore.getInstance()
    private var ogrenciler: List<Ogrenci> = listOf()
    private var dersId: String? = null
    private var ogretmen: Ogretmen? = null
    private var notTipi: NotTipi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_not)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewOgrenciler)
        kaydetButton = findViewById(R.id.buttonKaydet)
        textViewNotBaslik = findViewById(R.id.textViewNotBaslik)
        buttonGeri = findViewById(R.id.buttonGeri)
        sliderOrtakPuan = findViewById(R.id.sliderOrtakPuan)
        textViewOrtakPuan = findViewById(R.id.textViewOrtakPuan)
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

        // Başlığı not tipine göre ayarla
        textViewNotBaslik.text = notTipi?.Adi ?: "Not Girişi"

        ogrencileriGetirVeListele()

        kaydetButton.setOnClickListener {
            puanlariKaydet()
        }

        sliderOrtakPuan.addOnChangeListener { _, value, _ ->
            val intValue = value.toInt()
            textViewOrtakPuan.text = intValue.toString()
            if (::ogrenciAdapter.isInitialized) {
                ogrenciAdapter.tumPuanlariAyarla(intValue)
            }
        }
    }

    private fun ogrencileriGetirVeListele() {
        // Önce dersin SinifId'sini bul
        db.collection("Ders").document(dersId!!).get().addOnSuccessListener { dersDoc ->
            val sinifId = dersDoc.getString("SinifId")
            if (sinifId == null) {
                Toast.makeText(this, "Sınıf bulunamadı!", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            // Şimdi bu sınıfa ait öğrencileri çek
            db.collection("Ogrenci").whereEqualTo("SinifId", sinifId).get().addOnSuccessListener { ogrenciDocs ->
                ogrenciler = ogrenciDocs.map { doc ->
                    Ogrenci(
                        Id = doc.id,
                        Adi = doc.getString("Adi") ?: "",
                        SinifId = doc.getString("SinifId") ?: "",
                        OkulNo = doc.getString("OkulNo"),
                        Email = doc.getString("Email"),
                        Telefon = doc.getString("Telefon")
                    )
                }
                ogrenciAdapter = OgrenciPuanAdapter(ogrenciler)
                recyclerView.adapter = ogrenciAdapter
            }
        }
    }

    private fun puanlariKaydet() {
        val puanListesi = ogrenciAdapter.getPuanlar()
        // Önce yeni bir belge referansı oluştur
        val oturumRef = db.collection("NotOturumu").document()
        val notOturumu = hashMapOf(
            "Id" to oturumRef.id, // ID alanı olarak da ekle
            "DersId" to dersId,
            "OgretmenId" to ogretmen!!.Id,
            "NotTipiId" to notTipi!!.Id,
            "OlusturulmaTarihi" to com.google.firebase.Timestamp.now()
        )
        oturumRef.set(notOturumu).addOnSuccessListener {
            // Her öğrenci için not kaydı ekle
            val batch = db.batch()
            for ((ogrenciId, puan) in puanListesi) {
                val notRef = db.collection("Not").document()
                val not = hashMapOf(
                    "OgrenciId" to ogrenciId,
                    "NotOturumuId" to oturumRef.id,
                    "Puan" to puan,
                    "OlusturulmaTarihi" to com.google.firebase.Timestamp.now()
                )
                batch.set(notRef, not)
            }
            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Notlar kaydedildi!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Notlar kaydedilemedi!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Öğrenci ve puan girişi için adapter
