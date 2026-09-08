package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.ChatSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatSessionAdapter(
    private var sessions: MutableList<ChatSession>,
    private val onSessionClick: (ChatSession) -> Unit,
    private val onSessionDelete: (ChatSession) -> Unit
) : RecyclerView.Adapter<ChatSessionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    fun updateData(newSessions: List<ChatSession>) {
        sessions.clear()
        sessions.addAll(newSessions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int = sessions.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvSessionTitle)
        private val tvSnippet: TextView = itemView.findViewById(R.id.tvSessionSnippet)
        private val tvStack: TextView = itemView.findViewById(R.id.tvSessionStack)
        private val tvFeatures: TextView = itemView.findViewById(R.id.tvSessionFeaturesCount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvSessionDate)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnSessionDelete)

        fun bind(session: ChatSession) {
            tvTitle.text = session.title
            val lastMsg = session.messages.lastOrNull()?.text ?: session.appIdea
            tvSnippet.text = lastMsg.take(120).replace("\n", " ")
            tvStack.text = if (session.techStack.isNotBlank()) session.techStack else "AI Prompt"
            tvFeatures.text = "${session.features.size} Features"
            tvDate.text = dateFormat.format(Date(session.updatedAt))

            btnDelete.setOnClickListener {
                onSessionDelete(session)
            }

            itemView.setOnClickListener {
                onSessionClick(session)
            }
        }
    }
}
