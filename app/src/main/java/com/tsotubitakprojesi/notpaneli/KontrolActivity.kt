package com.tsotubitakprojesi.notpaneli

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tsotubitakprojesi.notpaneli.NotTipiAdapter


class KontrolActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notTipiAdapter: NotTipiAdapter
    private val db = FirebaseFirestore.getInstance()
    private var secilenDersId: String? = null
    private var mevcutOgretmen: Ogretmen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kontrol)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Intent'ten gelen verileri al
        secilenDersId = intent.getStringExtra("dersId")
        mevcutOgretmen = intent.getParcelableExtra("ogretmen")

        val buttonGeri = findViewById<ImageButton>(R.id.buttonGeri)
        buttonGeri.setOnClickListener {
            finish()
        }

        sinifVeDersAdiniGetir()

        recyclerView = findViewById(R.id.recyclerViewNotTipleri)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notTipiAdapter = NotTipiAdapter(listOf(),
            onNotTipiClick = { notTipi ->
                when (notTipi.Adi) {
                    "Ödev Kontrolü" -> {
                        val intent = Intent(this, OdevActivity::class.java).apply {
                            putExtra("dersId", secilenDersId)
                            putExtra("ogretmen", mevcutOgretmen)
                            putExtra("notTipi", notTipi)
                        }
                        startActivity(intent)
                    }
                    "Kıyafet Kontrolü" -> {
                        val intent = Intent(this, KiyafetKontrolActivity::class.java).apply {
                            putExtra("dersId", secilenDersId)
                            putExtra("ogretmen", mevcutOgretmen)
                            putExtra("notTipi", notTipi)
                        }
                        startActivity(intent)
                    }
                    "Uygulama Sinavi" -> {
                        val intent = Intent(this, SinavActivity::class.java).apply {
                            putExtra("dersId", secilenDersId)
                            putExtra("ogretmen", mevcutOgretmen)
                            putExtra("notTipi", notTipi)
                        }
                        startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(this, NotActivity::class.java).apply {
                            putExtra("dersId", secilenDersId)
                            putExtra("ogretmen", mevcutOgretmen)
                            putExtra("notTipi", notTipi)
                        }
                        startActivity(intent)
                    }
                }
            },
            onRaporClick = { notTipi ->
                val intent = Intent(this, NotRaporActivity::class.java).apply {
                    putExtra("notTipiId", notTipi.Id)
                    putExtra("dersId", secilenDersId)
                    putExtra("ogretmenId", mevcutOgretmen?.Id)
                }
                startActivity(intent)
            }
        )
        recyclerView.adapter = notTipiAdapter

        notTipleriniGetir()
    }

    private fun sinifVeDersAdiniGetir() {
        val textViewSinifDers = findViewById<TextView>(R.id.textViewSinifDers)
        if (secilenDersId == null) return
        db.collection("Ders").document(secilenDersId!!).get().addOnSuccessListener { dersDoc ->
            val dersAdi = dersDoc.getString("DersAdi") ?: ""
            val sinifId = dersDoc.getString("SinifId")
            if (sinifId != null) {
                db.collection("Sinif").document(sinifId).get().addOnSuccessListener { sinifDoc ->
                    val sinifAdi = sinifDoc.getString("Adi") ?: ""
                    textViewSinifDers.text = "$sinifAdi  /  $dersAdi"
                }
            }
        }
    }

    private fun notTipleriniGetir() {
        db.collection("NotTipi")
            .get()
            .addOnSuccessListener { documents ->
                val notTipleri = documents.map { doc ->
                    NotTipi(
                        Id = doc.id,
                        Adi = doc.getString("Adi") ?: "",
                        Aciklama = doc.getString("Aciklama") ?: ""
                    )
                }
                notTipiAdapter.notTipleriGuncelle(notTipleri)
            }
    }
}