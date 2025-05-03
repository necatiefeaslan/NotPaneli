package com.tsotubitakprojesi.notpaneli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider

class OgrenciPuanAdapter(private val ogrenciler: List<Ogrenci>, private val layoutId: Int) : RecyclerView.Adapter<OgrenciPuanAdapter.ViewHolder>() {
    private val puanlar = mutableMapOf<String, Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ogrenciAdi: TextView = view.findViewById(R.id.textViewOgrenciAdi)
        val sliderPuan: Slider = view.findViewById(R.id.sliderPuan)
        val textViewPuan: TextView = view.findViewById(R.id.textViewPuan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ogrenci = ogrenciler[position]
        holder.ogrenciAdi.text = ogrenci.Adi
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
        if (puan in 0..100) {
            ogrenciler.forEachIndexed { index, ogrenci ->
                if (puanlar[ogrenci.Id] != puan) {
                    puanlar[ogrenci.Id] = puan
                    notifyItemChanged(index)
                }
            }
        }
    }
}