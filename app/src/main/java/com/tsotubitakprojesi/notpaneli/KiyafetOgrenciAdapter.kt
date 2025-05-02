package com.tsotubitakprojesi.notpaneli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KiyafetOgrenciAdapter(private val tumOgrenciler: List<Ogrenci>) : RecyclerView.Adapter<KiyafetOgrenciAdapter.ViewHolder>() {
    private val kontroller = mutableMapOf<String, Boolean>()
    private var ogrenciler: List<Ogrenci> = tumOgrenciler.toList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ogrenciAdi: TextView = view.findViewById(R.id.textViewOgrenciAdi)
        val kiyafetSwitch: Switch = view.findViewById(R.id.switchKiyafetKontrol)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kiyafet_ogrenci, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ogrenci = ogrenciler[position]
        holder.ogrenciAdi.text = ogrenci.Adi
        holder.kiyafetSwitch.isChecked = kontroller[ogrenci.Id] ?: true
        holder.kiyafetSwitch.setOnCheckedChangeListener { _, isChecked ->
            kontroller[ogrenci.Id] = isChecked
        }
    }

    override fun getItemCount() = ogrenciler.size

    fun getKontroller(): Map<String, Boolean> = kontroller

    fun tumOgrencilerinDurumunuDegistir(uygun: Boolean) {
        ogrenciler.forEachIndexed { index, ogrenci ->
            if (kontroller[ogrenci.Id] != uygun) {
                kontroller[ogrenci.Id] = uygun
                notifyItemChanged(index)
            }
        }
    }

    fun filtrele(query: String) {
        ogrenciler = if (query.isBlank()) {
            tumOgrenciler
        } else {
            tumOgrenciler.filter { it.Adi.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
} 