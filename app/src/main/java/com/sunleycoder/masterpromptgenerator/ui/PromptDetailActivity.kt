package com.sunleycoder.masterpromptgenerator.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.local.PromptStorageManager
import com.sunleycoder.masterpromptgenerator.data.model.PromptHistoryItem
import com.sunleycoder.masterpromptgenerator.databinding.ActivityPromptDetailBinding
import com.sunleycoder.masterpromptgenerator.ui.dialog.AiTesterDialog

class PromptDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PROMPT_ITEM = "extra_prompt_item"
    }

    private lateinit var binding: ActivityPromptDetailBinding
    private lateinit var storage: PromptStorageManager
    private var promptItem: PromptHistoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromptDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = PromptStorageManager(this)
        promptItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_PROMPT_ITEM, PromptHistoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_PROMPT_ITEM) as? PromptHistoryItem
        }

        if (promptItem == null) {
            Toast.makeText(this, "Failed to load prompt details.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupActions()
    }

    private fun setupUI() {
        val item = promptItem ?: return

        binding.tvDetailTitle.text = item.title
        binding.tvDetailTechBadge.text = item.techStack
        binding.tvDetailTargetAiBadge.text = item.targetAi.split(" ").firstOrNull() ?: item.targetAi
        binding.tvDetailLengthBadge.text = "${item.fullPrompt.length} chars • ${item.fullPrompt.split("\\s+".toRegex()).size} words"
        binding.tvFullPromptText.text = item.fullPrompt

        updateStarVisual(item.isFavorite)

        binding.btnDetailBack.setOnClickListener {
            finish()
        }
    }

    private fun setupActions() {
        val item = promptItem ?: return

        binding.btnDetailCopy.setOnClickListener {
            copyToClipboard(item.fullPrompt)
        }

        binding.btnDetailShare.setOnClickListener {
            sharePrompt(item.title, item.fullPrompt)
        }

        binding.btnDetailStar.setOnClickListener {
            val isFav = storage.toggleFavorite(item.id)
            item.isFavorite = isFav
            updateStarVisual(isFav)
            val msg = if (isFav) "Added to Starred Prompts" else "Removed from Starred"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnDetailExport.setOnClickListener {
            val file = storage.exportToDownloads(item)
            if (file != null && file.exists()) {
                Toast.makeText(this, "Saved to Downloads: ${file.name}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Exported markdown to storage.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDetailTestAi.setOnClickListener {
            val dialog = AiTesterDialog(this, item.fullPrompt)
            dialog.show()
        }
    }

    private fun updateStarVisual(isFavorite: Boolean) {
        binding.btnDetailStar.setImageResource(
            if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star
        )
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Master Prompt", text)
        clipboard.setPrimaryClip(clip)

        try {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (e: Exception) {
            // Non-critical
        }

        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    private fun sharePrompt(title: String, text: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Share Master Prompt via"))
    }
}
