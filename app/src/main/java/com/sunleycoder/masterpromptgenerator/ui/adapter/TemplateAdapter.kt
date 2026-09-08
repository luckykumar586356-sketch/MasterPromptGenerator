package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.TemplateItem

class TemplateAdapter(
    private val templates: List<TemplateItem>,
    private val onTemplateClick: (TemplateItem) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_template_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(templates[position])
    }

    override fun getItemCount(): Int = templates.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEmoji: TextView = itemView.findViewById(R.id.tvTemplateEmoji)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTemplateTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvTemplateSubtitle)

        fun bind(item: TemplateItem) {
            tvEmoji.text = item.iconEmoji
            tvTitle.text = item.title
            tvSubtitle.text = item.idea

            itemView.setOnClickListener {
                onTemplateClick(item)
            }
        }
    }
}
