package com.tsotubitakprojesi.notpaneli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotRaporDetayAdapter(private val notlar: List<NotRaporDetayItem>) : RecyclerView.Adapter<NotRaporDetayAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPuanBadge: TextView = view.findViewById(R.id.textPuanBadge)
        val textOgrenciAdi: TextView = view.findViewById(R.id.textOgrenciAdi)
        val textPuanlar: TextView = view.findViewById(R.id.textPuanlar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_not_rapor_detay, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notlar[position]
        holder.textPuanBadge.text = item.puanlar.firstOrNull()?.toString() ?: ""
        holder.textOgrenciAdi.text = item.ogrenciAdi
        holder.textPuanlar.text = "Tüm Puanlar: ${item.puanlar.joinToString(", ")}"
    }

    override fun getItemCount() = notlar.size
} 