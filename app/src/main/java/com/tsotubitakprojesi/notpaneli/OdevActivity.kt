package com.tsotubitakprojesi.notpaneli

import android.os.Bundle
import android.widget.Button
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
import android.widget.EditText
import android.widget.SeekBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.ContextWrapper
import com.tsotubitakprojesi.notpaneli.*

class OdevActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var kaydetButton: Button
    private lateinit var odevOgrenciAdapter: OdevOgrenciAdapter
    private lateinit var buttonGeri: ImageButton
    private lateinit var switchTumunuYuzYap: Switch
    private val db = FirebaseFirestore.getInstance()
    private var ogrenciler: List<Ogrenci> = listOf()
    private var dersId: String? = null
    private var ogretmen: Ogretmen? = null
    private var notTipi: NotTipi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_odev)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewOgrenciler)
        kaydetButton = findViewById(R.id.buttonKaydet)
        buttonGeri = findViewById(R.id.buttonGeri)
        switchTumunuYuzYap = findViewById(R.id.switchTumunuYuzYap)
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

        kaydetButton.setOnClickListener {
            puanlariKaydet()
        }

        switchTumunuYuzYap.setOnCheckedChangeListener { _, isChecked ->
            if (::odevOgrenciAdapter.isInitialized) {
                if (isChecked) {
                    odevOgrenciAdapter.tumPuanlariAyarla(100)
                } else {
                    odevOgrenciAdapter.tumPuanlariAyarla(0)
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
                odevOgrenciAdapter = OdevOgrenciAdapter(ogrenciler, this)
                recyclerView.adapter = odevOgrenciAdapter
            }
        }
    }

    private fun puanlariKaydet() {
        val puanListesi = odevOgrenciAdapter.getPuanlar()
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

class OdevOgrenciAdapter(private val ogrenciler: List<Ogrenci>, private val context: android.content.Context) : RecyclerView.Adapter<OdevOgrenciAdapter.ViewHolder>() {
    private val puanlar = mutableMapOf<String, Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ogrenciAdi: TextView = view.findViewById(R.id.textViewOgrenciAdi)
        val sliderPuan: com.google.android.material.slider.Slider = view.findViewById(R.id.sliderPuan)
        val textViewPuan: TextView = view.findViewById(R.id.textViewPuan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ogrenci_puan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ogrenci = ogrenciler[position]
        holder.ogrenciAdi.text = ogrenci.Adi
        
        // Slider'ı 0, 50 ve 100 değerlerini alacak şekilde ayarla
        holder.sliderPuan.stepSize = 50f
        holder.sliderPuan.valueFrom = 0f
        holder.sliderPuan.valueTo = 100f
        
        val puan = puanlar[ogrenci.Id] ?: 0
        holder.sliderPuan.value = puan.toFloat()
        holder.textViewPuan.text = puan.toString()
        
        holder.sliderPuan.addOnChangeListener { _, value, _ ->
            val intValue = value.toInt()
            holder.textViewPuan.text = intValue.toString()
            puanlar[ogrenci.Id] = intValue
        }
    }

    override fun getItemCount() = ogrenciler.size

    fun getPuanlar(): Map<String, Int> = puanlar

    fun tumPuanlariAyarla(puan: Int) {
        if (puan in listOf(0, 50, 100)) {
            ogrenciler.forEachIndexed { index, ogrenci ->
                if (puanlar[ogrenci.Id] != puan) {
                    puanlar[ogrenci.Id] = puan
                    notifyItemChanged(index)
                }
            }
        } else {
            Toast.makeText(context, "Not 0, 50 veya 100 olmalıdır!", Toast.LENGTH_SHORT).show()
        }
    }
}