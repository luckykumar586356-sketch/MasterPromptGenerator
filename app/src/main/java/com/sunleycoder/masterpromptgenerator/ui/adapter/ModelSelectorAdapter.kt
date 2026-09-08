package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.ModelInfo

class ModelSelectorAdapter(
    private var models: List<ModelInfo>,
    private val selectedModelId: String,
    private val onModelSelected: (ModelInfo) -> Unit
) : RecyclerView.Adapter<ModelSelectorAdapter.ViewHolder>() {

    fun updateModels(newModels: List<ModelInfo>) {
        models = newModels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(models[position])
    }

    override fun getItemCount(): Int = models.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvModelDisplayName)
        private val tvBadge: TextView = itemView.findViewById(R.id.tvModelTierBadge)
        private val tvId: TextView = itemView.findViewById(R.id.tvModelId)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvModelDescription)
        private val tvContext: TextView = itemView.findViewById(R.id.tvModelContext)

        fun bind(item: ModelInfo) {
            tvName.text = item.displayName
            tvId.text = item.modelId
            tvBadge.text = item.tierLabel
            tvDesc.text = item.description
            tvContext.text = item.contextWindow

            val context = itemView.context
            if (item.isFree) {
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.accent))
            } else {
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.secondary))
            }

            val isSelected = item.modelId.equals(selectedModelId, ignoreCase = true)
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_rounded_card)
                tvName.setTextColor(ContextCompat.getColor(context, R.color.primary))
            } else {
                tvName.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            }

            itemView.setOnClickListener {
                onModelSelected(item)
            }
        }
    }
}
