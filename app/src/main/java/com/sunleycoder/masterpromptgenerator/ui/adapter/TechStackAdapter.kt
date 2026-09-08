package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.TechStack

class TechStackAdapter(
    private val stacks: List<TechStack>,
    private var selectedId: String = "flutter",
    private val onStackSelected: (TechStack) -> Unit
) : RecyclerView.Adapter<TechStackAdapter.ViewHolder>() {

    fun setSelectedStack(id: String) {
        selectedId = id
        notifyDataSetChanged()
    }

    fun getSelectedStack(): TechStack {
        return TechStack.findById(selectedId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feature_chip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stacks[position])
    }

    override fun getItemCount(): Int = stacks.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLabel: TextView = itemView.findViewById(R.id.tvChipLabel)
        private val ivCheck: View = itemView.findViewById(R.id.ivChipCheck)

        fun bind(stack: TechStack) {
            tvLabel.text = "${stack.iconEmoji} ${stack.name}"
            val isSelected = stack.id.equals(selectedId, ignoreCase = true)
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_chip_selected)
                tvLabel.setTextColor(ContextCompat.getColor(itemView.context, R.color.chip_selected_text))
                ivCheck.visibility = View.VISIBLE
            } else {
                itemView.setBackgroundResource(R.drawable.bg_chip_unselected)
                tvLabel.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
                ivCheck.visibility = View.GONE
            }

            itemView.setOnClickListener {
                selectedId = stack.id
                notifyDataSetChanged()
                onStackSelected(stack)
            }
        }
    }
}
