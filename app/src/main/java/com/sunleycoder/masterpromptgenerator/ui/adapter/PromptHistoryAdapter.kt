package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.PromptHistoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PromptHistoryAdapter(
    private var items: MutableList<PromptHistoryItem>,
    private val onItemClick: (PromptHistoryItem) -> Unit,
    private val onFavoriteToggle: (PromptHistoryItem) -> Unit,
    private val onDeleteClick: (PromptHistoryItem) -> Unit
) : RecyclerView.Adapter<PromptHistoryAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())

    fun updateData(newItems: List<PromptHistoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prompt_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvHistoryTitle)
        private val tvDate: TextView = itemView.findViewById(R.id.tvHistoryDate)
        private val tvTechStack: TextView = itemView.findViewById(R.id.tvHistoryTech)
        private val tvFeaturesCount: TextView = itemView.findViewById(R.id.tvHistoryFeatures)
        private val tvPreview: TextView = itemView.findViewById(R.id.tvHistoryPreview)
        private val btnStar: ImageView = itemView.findViewById(R.id.btnHistoryStar)
        private val btnCopy: ImageView = itemView.findViewById(R.id.btnHistoryCopy)
        private val btnShare: ImageView = itemView.findViewById(R.id.btnHistoryShare)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnHistoryDelete)

        fun bind(item: PromptHistoryItem) {
            tvTitle.text = item.title
            tvDate.text = dateFormat.format(Date(item.timestamp))
            tvTechStack.text = item.techStack
            tvFeaturesCount.text = "${item.features.size} Features"
            tvPreview.text = item.fullPrompt.take(180).replace("\n", " ") + "…"

            btnStar.setImageResource(if (item.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star)

            btnStar.setOnClickListener {
                onFavoriteToggle(item)
                btnStar.setImageResource(if (item.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star)
            }

            btnCopy.setOnClickListener {
                val clipboard = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Master Prompt", item.fullPrompt)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(itemView.context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
            }

            btnShare.setOnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, item.title)
                    putExtra(Intent.EXTRA_TEXT, item.fullPrompt)
                    type = "text/plain"
                }
                itemView.context.startActivity(Intent.createChooser(sendIntent, "Share Master Prompt"))
            }

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
