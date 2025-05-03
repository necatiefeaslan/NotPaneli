package com.tsotubitakprojesi.notpaneli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

// Model: Öğrenci adı, puan, tarih
class NotRaporAdapter(
    private val notlar: List<NotRaporItem>,
    private val onTarihClick: (String) -> Unit
) : RecyclerView.Adapter<NotRaporAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTarih: TextView = view.findViewById(R.id.textTarih)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_not_rapor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notlar[position]
        holder.textTarih.text = item.tarih
        val clickListener = View.OnClickListener {
            onTarihClick(item.tarih)
        }
        holder.textTarih.setOnClickListener(clickListener)
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun getItemCount() = notlar.size
}

data class NotRaporItem(
    val ogrenciAdi: String,
    val puan: Int,
    val tarih: String
) 