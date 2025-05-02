package com.tsotubitakprojesi.notpaneli

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tsotubitakprojesi.notpaneli.SinifDers
import com.tsotubitakprojesi.notpaneli.SinifDersAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewHosgeldin: TextView
    private val sinifDersList = mutableListOf<SinifDers>()
    private lateinit var adapter: SinifDersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Kullanıcı giriş yapmamışsa login ekranına yönlendir
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Çıkış butonunu ayarla
        findViewById<MaterialButton>(R.id.buttonLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerViewSiniflar)
        textViewHosgeldin = findViewById(R.id.textViewHosgeldin)
        val textViewOgretmen = findViewById<TextView>(R.id.textViewOgretmen)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ogretmen bilgisini Firestore'dan çek
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser ?: return
        db.collection("Ogretmenler").document(user.uid).get().addOnSuccessListener { doc ->
            val ogretmen = doc.toObject(Ogretmen::class.java)
            if (ogretmen != null) {
                textViewHosgeldin.text = "Hoş geldiniz,"
                textViewOgretmen.text = ogretmen.Adi
                getSiniflarVeDersler(ogretmen)
            }
        }
    }

    private fun getSiniflarVeDersler(ogretmen: Ogretmen) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val ogretmenId = user.uid

        // Önce bu öğretmene ait dersleri çek
        db.collection("Ders")
            .whereEqualTo("OgretmenId", ogretmenId)
            .get()
            .addOnSuccessListener { derslerSnapshot ->
                val dersler = derslerSnapshot.documents.mapNotNull { it.toObject(Ders::class.java) }
                val sinifIdSet = dersler.map { it.SinifId }.toSet()

                if (sinifIdSet.isEmpty()) {
                    sinifDersList.clear()
                    adapter = SinifDersAdapter(sinifDersList, ogretmen)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                db.collection("Sinif")
                    .whereIn("Id", sinifIdSet.toList())
                    .get()
                    .addOnSuccessListener { siniflarSnapshot ->
                        val siniflar = siniflarSnapshot.documents.mapNotNull { it.toObject(Sinif::class.java) }
                        sinifDersList.clear()
                        for (sinif in siniflar) {
                            val ilgiliDersler = dersler.filter { it.SinifId == sinif.Id }
                            sinifDersList.add(SinifDers(sinif, ilgiliDersler))
                        }
                        adapter = SinifDersAdapter(sinifDersList, ogretmen)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Log.e("MainActivity", "Sınıflar alınamadı", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Dersler alınamadı", e)
            }
    }
}
