package com.tsotubitakprojesi.notpaneli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Context
import androidx.recyclerview.widget.RecyclerView

class SinavOgrenciAdapter(
    private val ogrenciler: List<Ogrenci>,
    private val context: Context,
    private val onPuanChanged: (String, Int) -> Unit
) : RecyclerView.Adapter<SinavOgrenciAdapter.ViewHolder>() {
    private val puanlar = mutableMapOf<String, Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ogrenciAdi: TextView = view.findViewById(R.id.textViewOgrenciAdi)
        val puanInput: EditText = view.findViewById(R.id.editTextPuan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sinav_ogrenci, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ogrenci = ogrenciler[position]
        holder.ogrenciAdi.text = ogrenci.Adi
        holder.puanInput.setText(puanlar[ogrenci.Id]?.toString() ?: "")
        
        holder.puanInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val puan = s?.toString()?.toIntOrNull() ?: 0
                if (puan in 0..100) {
                    if (puanlar[ogrenci.Id] != puan) {
                        puanlar[ogrenci.Id] = puan
                        onPuanChanged(ogrenci.Id, puan)
                    }
                } else {
                    Toast.makeText(context, "Not 0-100 arasında olmalıdır!", Toast.LENGTH_SHORT).show()
                    holder.puanInput.setText("0")
                    puanlar[ogrenci.Id] = 0
                    onPuanChanged(ogrenci.Id, 0)
                }
            }
            
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Kullanıcı 3 karakterden fazla girmeye çalışırsa engelle
                if (s?.length ?: 0 >= 3 && after > 0) {
                    holder.puanInput.setText(s?.subSequence(0, 3))
                }
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount() = ogrenciler.size

    fun getPuanlar(): Map<String, Int> = puanlar

    fun tumPuanlariAyarla(puan: Int) {
        if (puan in 0..100) {
            ogrenciler.forEachIndexed { index, ogrenci ->
                if (puanlar[ogrenci.Id] != puan) {
                    puanlar[ogrenci.Id] = puan
                    notifyItemChanged(index)
                }
            }
        } else {
            Toast.makeText(context, "Not 0-100 arasında olmalıdır!", Toast.LENGTH_SHORT).show()
        }
    }
}