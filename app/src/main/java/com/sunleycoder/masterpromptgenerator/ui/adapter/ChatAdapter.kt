package com.sunleycoder.masterpromptgenerator.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.AppFeature
import com.sunleycoder.masterpromptgenerator.data.model.ChatMessage
import com.sunleycoder.masterpromptgenerator.data.model.MessageType
import com.sunleycoder.masterpromptgenerator.data.model.TechStack
import com.sunleycoder.masterpromptgenerator.ui.dialog.AiTesterDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val messages: MutableList<ChatMessage>,
    private val onTechSelected: (ChatMessage, TechStack) -> Unit,
    private val onFeaturesSelected: (ChatMessage, List<String>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_BOT_TEXT = 2
        private const val TYPE_BOT_TECH_CHOICE = 3
        private const val TYPE_BOT_FEATURE_CHOICE = 4
        private const val TYPE_BOT_GENERATING = 5
        private const val TYPE_BOT_FINAL_PROMPT = 6
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].type) {
            MessageType.USER -> TYPE_USER
            MessageType.BOT_TEXT -> TYPE_BOT_TEXT
            MessageType.BOT_TECH_STACK_CHOICE -> TYPE_BOT_TECH_CHOICE
            MessageType.BOT_FEATURE_CHOICE -> TYPE_BOT_FEATURE_CHOICE
            MessageType.BOT_GENERATING -> TYPE_BOT_GENERATING
            MessageType.BOT_FINAL_PROMPT -> TYPE_BOT_FINAL_PROMPT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> UserViewHolder(inflater.inflate(R.layout.item_chat_user, parent, false))
            TYPE_BOT_TEXT -> BotTextViewHolder(inflater.inflate(R.layout.item_chat_bot_text, parent, false))
            TYPE_BOT_TECH_CHOICE -> TechChoiceViewHolder(inflater.inflate(R.layout.item_chat_tech_choice, parent, false))
            TYPE_BOT_FEATURE_CHOICE -> FeatureChoiceViewHolder(inflater.inflate(R.layout.item_chat_feature_choice, parent, false))
            TYPE_BOT_GENERATING -> GeneratingViewHolder(inflater.inflate(R.layout.item_chat_generating, parent, false))
            TYPE_BOT_FINAL_PROMPT -> FinalPromptViewHolder(inflater.inflate(R.layout.item_chat_final_prompt, parent, false))
            else -> BotTextViewHolder(inflater.inflate(R.layout.item_chat_bot_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserViewHolder -> holder.bind(message)
            is BotTextViewHolder -> holder.bind(message)
            is TechChoiceViewHolder -> holder.bind(message)
            is FeatureChoiceViewHolder -> holder.bind(message)
            is GeneratingViewHolder -> { /* animated indicator */ }
            is FinalPromptViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvUserTime)

        fun bind(item: ChatMessage) {
            tvMessage.text = item.text
            tvTime.text = timeFormat.format(Date(item.timestamp))
        }
    }

    inner class BotTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvBotMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvBotTime)

        fun bind(item: ChatMessage) {
            tvMessage.text = item.text
            tvTime.text = timeFormat.format(Date(item.timestamp))
        }
    }

    inner class TechChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuestion: TextView = itemView.findViewById(R.id.tvTechQuestion)
        private val layoutChips: LinearLayout = itemView.findViewById(R.id.layoutTechChips)
        private val btnContinue: MaterialButton = itemView.findViewById(R.id.btnContinueTech)

        fun bind(item: ChatMessage) {
            if (item.text.isNotBlank()) {
                tvQuestion.text = item.text
            }

            if (item.selectedTechStack == null) {
                item.selectedTechStack = "flutter"
            }

            layoutChips.removeAllViews()
            val context = itemView.context

            TechStack.ALL.forEach { stack ->
                val chipView = LayoutInflater.from(context).inflate(R.layout.item_feature_chip, layoutChips, false)
                val tvLabel = chipView.findViewById<TextView>(R.id.tvChipLabel)
                val ivCheck = chipView.findViewById<View>(R.id.ivChipCheck)

                tvLabel.text = "${stack.iconEmoji} ${stack.name}"
                val isSelected = stack.id.equals(item.selectedTechStack, ignoreCase = true)

                if (isSelected) {
                    chipView.setBackgroundResource(R.drawable.bg_chip_selected)
                    tvLabel.setTextColor(ContextCompat.getColor(context, R.color.chip_selected_text))
                    ivCheck.visibility = View.VISIBLE
                } else {
                    chipView.setBackgroundResource(R.drawable.bg_chip_unselected)
                    tvLabel.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    ivCheck.visibility = View.GONE
                }

                if (!item.isSubmitted) {
                    chipView.setOnClickListener {
                        item.selectedTechStack = stack.id
                        bind(item)
                    }
                } else {
                    chipView.isClickable = false
                }

                layoutChips.addView(chipView)
            }

            val currentSelected = TechStack.findById(item.selectedTechStack ?: "flutter")
            btnContinue.text = "Continue with ${currentSelected.name} ➔"

            if (item.isSubmitted) {
                btnContinue.visibility = View.GONE
            } else {
                btnContinue.visibility = View.VISIBLE
                btnContinue.setOnClickListener {
                    item.isSubmitted = true
                    btnContinue.visibility = View.GONE
                    onTechSelected(item, currentSelected)
                }
            }
        }
    }

    inner class FeatureChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuestion: TextView = itemView.findViewById(R.id.tvFeatureQuestion)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvFeatureCountStatus)
        private val layoutChips: LinearLayout = itemView.findViewById(R.id.layoutFeatureChips)
        private val btnSelectAll: TextView = itemView.findViewById(R.id.btnChatSelectAllFeatures)
        private val btnClear: TextView = itemView.findViewById(R.id.btnChatClearFeatures)
        private val btnSubmit: MaterialButton = itemView.findViewById(R.id.btnSubmitFeatures)

        fun bind(item: ChatMessage) {
            if (item.text.isNotBlank()) {
                tvQuestion.text = item.text
            }

            val context = itemView.context
            val allFeatures = AppFeature.getDefaultList()

            // Pre-select default essential features if empty
            if (item.featureIds.isEmpty() && !item.isSubmitted) {
                item.featureIds.addAll(listOf("auth", "tournament", "payments", "live_updates", "chat", "user_profile"))
            }

            tvStatus.text = "${item.featureIds.size} features selected"

            layoutChips.removeAllViews()

            allFeatures.forEach { feat ->
                val checkView = CheckBox(context).apply {
                    text = "${feat.emoji} ${feat.name} — ${feat.description}"
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    textSize = 12.5f
                    isChecked = item.featureIds.contains(feat.id)
                    isEnabled = !item.isSubmitted
                    setPadding(12, 10, 12, 10)

                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            if (!item.featureIds.contains(feat.id)) item.featureIds.add(feat.id)
                        } else {
                            item.featureIds.remove(feat.id)
                        }
                        tvStatus.text = "${item.featureIds.size} features selected"
                    }
                }
                layoutChips.addView(checkView)
            }

            if (item.isSubmitted) {
                btnSelectAll.visibility = View.GONE
                btnClear.visibility = View.GONE
                btnSubmit.visibility = View.GONE
            } else {
                btnSelectAll.visibility = View.VISIBLE
                btnClear.visibility = View.VISIBLE
                btnSubmit.visibility = View.VISIBLE

                btnSelectAll.setOnClickListener {
                    item.featureIds.clear()
                    item.featureIds.addAll(allFeatures.map { it.id })
                    bind(item)
                }

                btnClear.setOnClickListener {
                    item.featureIds.clear()
                    bind(item)
                }

                btnSubmit.setOnClickListener {
                    item.isSubmitted = true
                    btnSubmit.visibility = View.GONE
                    btnSelectAll.visibility = View.GONE
                    btnClear.visibility = View.GONE
                    onFeaturesSelected(item, item.featureIds)
                }
            }
        }
    }

    inner class GeneratingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class FinalPromptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTechBadge: TextView = itemView.findViewById(R.id.tvFinalPromptTechBadge)
        private val tvContent: TextView = itemView.findViewById(R.id.tvFinalPromptContent)
        private val btnCopy: MaterialButton = itemView.findViewById(R.id.btnFinalPromptCopy)
        private val btnShare: MaterialButton = itemView.findViewById(R.id.btnFinalPromptShare)
        private val btnTest: MaterialButton = itemView.findViewById(R.id.btnFinalPromptTest)

        fun bind(item: ChatMessage) {
            val promptText = item.promptContent ?: item.text
            tvContent.text = promptText
            tvTechBadge.text = item.selectedTechStack ?: "Production Stack"

            btnCopy.setOnClickListener {
                copyPrompt(itemView.context, promptText)
            }

            btnShare.setOnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, "Master Prompt")
                    putExtra(Intent.EXTRA_TEXT, promptText)
                    type = "text/plain"
                }
                itemView.context.startActivity(Intent.createChooser(sendIntent, "Share Master Prompt"))
            }

            btnTest.setOnClickListener {
                AiTesterDialog(itemView.context, promptText).show()
            }
        }

        private fun copyPrompt(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Master Prompt", text)
            clipboard.setPrimaryClip(clip)

            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40)
                }
            } catch (e: Exception) {
                // Ignore
            }

            Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }
}
