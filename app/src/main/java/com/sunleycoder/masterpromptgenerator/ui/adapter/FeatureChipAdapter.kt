package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.AppFeature

class FeatureChipAdapter(
    private val features: List<AppFeature>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<FeatureChipAdapter.ViewHolder>() {

    fun selectAll() {
        features.forEach { it.isSelected = true }
        notifyDataSetChanged()
        notifyChange()
    }

    fun clearAll() {
        features.forEach { it.isSelected = false }
        notifyDataSetChanged()
        notifyChange()
    }

    fun setSelectedIds(ids: List<String>) {
        features.forEach { feat ->
            feat.isSelected = ids.contains(feat.id)
        }
        notifyDataSetChanged()
        notifyChange()
    }

    fun getSelectedFeatureIds(): List<String> {
        return features.filter { it.isSelected }.map { it.id }
    }

    private fun notifyChange() {
        onSelectionChanged(getSelectedFeatureIds())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feature_chip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(features[position])
    }

    override fun getItemCount(): Int = features.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLabel: TextView = itemView.findViewById(R.id.tvChipLabel)
        private val ivCheck: ImageView = itemView.findViewById(R.id.ivChipCheck)

        fun bind(feature: AppFeature) {
            tvLabel.text = "${feature.emoji} ${feature.name}"
            if (feature.isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_chip_selected)
                tvLabel.setTextColor(ContextCompat.getColor(itemView.context, R.color.chip_selected_text))
                ivCheck.visibility = View.VISIBLE
            } else {
                itemView.setBackgroundResource(R.drawable.bg_chip_unselected)
                tvLabel.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
                ivCheck.visibility = View.GONE
            }

            itemView.setOnClickListener {
                feature.isSelected = !feature.isSelected
                notifyItemChanged(adapterPosition)
                notifyChange()
            }
        }
    }
}
