package com.tsotubitakprojesi.notpaneli

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import android.widget.TextView

class KiyafetKontrolActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var kaydetButton: Button
    private lateinit var buttonGeri: ImageButton
    private lateinit var textViewBaslik: TextView
    private lateinit var editTextOgrenciAra: EditText
    private lateinit var switchTumunuUygunYap: Switch
    private lateinit var kiyafetOgrenciAdapter: KiyafetOgrenciAdapter
    private val db = FirebaseFirestore.getInstance()
    private var ogrenciler: List<Ogrenci> = listOf()
    private var dersId: String? = null
    private var ogretmen: Ogretmen? = null
    private var notTipi: NotTipi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kiyafet_kontrol)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewOgrenciler)
        kaydetButton = findViewById(R.id.buttonKaydet)
        buttonGeri = findViewById(R.id.buttonGeri)
        textViewBaslik = findViewById(R.id.textViewBaslik)
        editTextOgrenciAra = findViewById(R.id.editTextOgrenciAra)
        switchTumunuUygunYap = findViewById(R.id.switchTumunuUygunYap)
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

        textViewBaslik.text = notTipi?.Adi ?: "Kıyafet Kontrolü"

        ogrencileriGetirVeListele()

        kaydetButton.setOnClickListener {
            kontrolleriKaydet()
        }

        switchTumunuUygunYap.setOnCheckedChangeListener { _, isChecked ->
            kiyafetOgrenciAdapter.tumOgrencilerinDurumunuDegistir(isChecked)
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
                kiyafetOgrenciAdapter = KiyafetOgrenciAdapter(ogrenciler)
                recyclerView.adapter = kiyafetOgrenciAdapter

                // Arama çubuğu dinleyicisi
                editTextOgrenciAra.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        kiyafetOgrenciAdapter.filtrele(s?.toString() ?: "")
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        }
    }

    private fun kontrolleriKaydet() {
        val kontrolListesi = kiyafetOgrenciAdapter.getKontroller()
        val oturumRef = db.collection("NotOturumu").document()
        val notOturumu = hashMapOf(
            "Id" to oturumRef.id,
            "DersId" to dersId,
            "OgretmenId" to ogretmen!!.Id,
            "NotTipiId" to notTipi!!.Id,
            "OlusturulmaTarihi" to com.google.firebase.Timestamp.now()
        )
        oturumRef.set(notOturumu).addOnSuccessListener {
            val batch = db.batch()
            for ((ogrenciId, uygun) in kontrolListesi) {
                val notRef = db.collection("Not").document()
                val not = hashMapOf(
                    "OgrenciId" to ogrenciId,
                    "NotOturumuId" to oturumRef.id,
                    "Puan" to if (uygun) 100 else 0,
                    "OlusturulmaTarihi" to com.google.firebase.Timestamp.now()
                )
                batch.set(notRef, not)
            }
            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Kıyafet kontrolleri kaydedildi!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Kıyafet kontrolleri kaydedilemedi!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

