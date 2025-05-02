package com.tsotubitakprojesi.notpaneli

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsotubitakprojesi.notpaneli.databinding.ItemSinifDersBinding

// Sınıf ve ilişkili dersleri birlikte tutan veri modeli
data class SinifDers(val sinif: Sinif, val dersler: List<Ders>)

class SinifDersAdapter(private val sinifDersList: List<SinifDers>, private val ogretmen: Ogretmen) : RecyclerView.Adapter<SinifDersAdapter.SinifDersViewHolder>() {

    inner class SinifDersViewHolder(val binding: ItemSinifDersBinding) : RecyclerView.ViewHolder(binding.root)

    private val expandedPositionSet = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SinifDersViewHolder {
        val binding = ItemSinifDersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SinifDersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SinifDersViewHolder, position: Int) {
        val item = sinifDersList[position]
        holder.binding.textSinifAdi.text = item.sinif.Adi

        // Dersleri temizle
        holder.binding.layoutDersler.removeAllViews()

        // Eğer kart açıksa dersleri göster
        if (expandedPositionSet.contains(position)) {
            item.dersler.forEach { ders ->
                val textView = TextView(holder.itemView.context).apply {
                    text = ders.DersAdi
                    textSize = 16f
                    setPadding(8, 8, 8, 8)
                    setOnClickListener {
                        val context = holder.itemView.context
                        val intent = Intent(context, KontrolActivity::class.java)
                        intent.putExtra("dersId", ders.Id)
                        intent.putExtra("ogretmen", ogretmen)
                        context.startActivity(intent)
                    }
                }
                holder.binding.layoutDersler.addView(textView)
            }
            holder.binding.layoutDersler.visibility = View.VISIBLE
        } else {
            holder.binding.layoutDersler.visibility = View.GONE
        }

        // Kart tıklanınca aç/kapat
        holder.itemView.setOnClickListener {
            if (expandedPositionSet.contains(position)) {
                expandedPositionSet.remove(position)
            } else {
                expandedPositionSet.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = sinifDersList.size
}
