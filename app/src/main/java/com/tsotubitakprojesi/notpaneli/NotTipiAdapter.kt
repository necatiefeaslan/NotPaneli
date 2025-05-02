package com.tsotubitakprojesi.notpaneli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotTipiAdapter(
    private var notTipleri: List<NotTipi>,
    private val onNotTipiClick: (NotTipi) -> Unit,
    private val onRaporClick: (NotTipi) -> Unit
) : RecyclerView.Adapter<NotTipiAdapter.NotTipiViewHolder>() {

    class NotTipiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val notTipiAdi: TextView = view.findViewById(R.id.textViewNotTipiAdi)
        val imageViewRapor: View = view.findViewById(R.id.imageViewRapor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotTipiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_not_tipi, parent, false)
        return NotTipiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotTipiViewHolder, position: Int) {
        val notTipi = notTipleri[position]
        holder.notTipiAdi.text = notTipi.Adi
        holder.itemView.setOnClickListener { onNotTipiClick(notTipi) }
        holder.imageViewRapor.setOnClickListener { onRaporClick(notTipi) }
    }

    override fun getItemCount() = notTipleri.size

    fun notTipleriGuncelle(yeniListe: List<NotTipi>) {
        notTipleri = yeniListe
        notifyDataSetChanged()
    }
} 