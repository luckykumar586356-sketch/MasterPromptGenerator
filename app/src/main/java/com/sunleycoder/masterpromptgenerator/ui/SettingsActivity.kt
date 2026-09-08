package com.sunleycoder.masterpromptgenerator.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider
import com.sunleycoder.masterpromptgenerator.data.model.ModelInfo
import com.sunleycoder.masterpromptgenerator.databinding.ActivitySettingsBinding
import com.sunleycoder.masterpromptgenerator.generator.AiApiService
import com.sunleycoder.masterpromptgenerator.ui.dialog.ModelSelectorDialog
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: AppPreferences
    private val apiService = AiApiService()
    private var currentModelId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        setupToolbar()
        loadCurrentSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.btnSettingsBack.setOnClickListener {
            finish()
        }
    }

    private fun loadCurrentSettings() {
        when (prefs.activeProvider) {
            AiProvider.OFFLINE_MASTER -> binding.rbOfflineMaster.isChecked = true
            AiProvider.GROQ -> binding.rbGroq.isChecked = true
            AiProvider.GEMINI -> binding.rbGemini.isChecked = true
            AiProvider.OPENROUTER -> binding.rbOpenRouter.isChecked = true
            AiProvider.OPENAI -> binding.rbOpenAi.isChecked = true
        }

        currentModelId = prefs.selectedModel
        updateProviderView(prefs.activeProvider)
    }

    private fun updateProviderView(provider: AiProvider) {
        if (provider == AiProvider.OFFLINE_MASTER) {
            binding.cardApiGuide.visibility = View.GONE
            binding.cardApiConfig.visibility = View.GONE
        } else {
            binding.cardApiGuide.visibility = View.VISIBLE
            binding.cardApiConfig.visibility = View.VISIBLE

            val currentKey = when (provider) {
                AiProvider.GROQ -> prefs.groqApiKey
                AiProvider.GEMINI -> prefs.geminiApiKey
                AiProvider.OPENROUTER -> prefs.openrouterApiKey
                AiProvider.OPENAI -> prefs.openaiApiKey
                AiProvider.OFFLINE_MASTER -> ""
            }
            binding.etApiKey.setText(currentKey)
            binding.tvApiKeyLabel.text = "${provider.displayName} API Key:"

            // Update Guide info
            binding.tvGuideTitle.text = "🔑 ${provider.displayName} API Key Kahan Milegi?"
            binding.tvGuideContent.text = provider.helpGuideHindi

            if (provider.keyUrl.isNotBlank()) {
                binding.btnOpenApiKeyUrl.visibility = View.VISIBLE
                binding.btnOpenApiKeyUrl.text = "🌐 Get Free ${provider.name} Key (Click to Open Webpage)"
                binding.btnOpenApiKeyUrl.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(provider.keyUrl))
                    startActivity(intent)
                }
            } else {
                binding.btnOpenApiKeyUrl.visibility = View.GONE
            }

            // Find or set default model for this provider
            val curated = ModelInfo.getCuratedModels(provider)
            val matchedModel = curated.find { it.modelId.equals(currentModelId, ignoreCase = true) }
                ?: curated.firstOrNull()

            if (matchedModel != null) {
                currentModelId = matchedModel.modelId
                displaySelectedModel(matchedModel)
            } else {
                currentModelId = prefs.selectedModel.ifBlank { "default_model" }
                binding.tvCurrentModelDisplayName.text = currentModelId
                binding.tvCurrentModelId.text = currentModelId
                binding.tvCurrentModelTierBadge.text = "MODEL ACTIVE"
            }
        }
        binding.tvTestConnectionResult.visibility = View.GONE
    }

    private fun displaySelectedModel(model: ModelInfo) {
        binding.tvCurrentModelDisplayName.text = model.displayName
        binding.tvCurrentModelId.text = model.modelId
        binding.tvCurrentModelTierBadge.text = model.tierLabel

        if (model.isFree) {
            binding.tvCurrentModelTierBadge.setTextColor(ContextCompat.getColor(this, R.color.accent))
        } else {
            binding.tvCurrentModelTierBadge.setTextColor(ContextCompat.getColor(this, R.color.secondary))
        }
    }

    private fun setupListeners() {
        binding.rgAiProvider.setOnCheckedChangeListener { _, checkedId ->
            val provider = when (checkedId) {
                binding.rbGroq.id -> AiProvider.GROQ
                binding.rbGemini.id -> AiProvider.GEMINI
                binding.rbOpenRouter.id -> AiProvider.OPENROUTER
                binding.rbOpenAi.id -> AiProvider.OPENAI
                else -> AiProvider.OFFLINE_MASTER
            }
            updateProviderView(provider)
        }

        // Open Model Selector Dialog on tapping model card or browse button
        val openModelSelector = View.OnClickListener {
            val provider = getSelectedProvider()
            val apiKey = binding.etApiKey.text.toString().trim()
            val dialog = ModelSelectorDialog(
                context = this,
                provider = provider,
                apiKey = apiKey,
                currentSelectedId = currentModelId
            ) { chosen ->
                currentModelId = chosen.modelId
                displaySelectedModel(chosen)
                Toast.makeText(this, "Selected: ${chosen.displayName} (${chosen.tierLabel})", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

        binding.layoutSelectModelCard.setOnClickListener(openModelSelector)
        binding.btnBrowseModels.setOnClickListener(openModelSelector)

        binding.btnTestConnection.setOnClickListener {
            val provider = getSelectedProvider()
            val apiKey = binding.etApiKey.text.toString().trim()

            binding.tvTestConnectionResult.visibility = View.VISIBLE
            binding.tvTestConnectionResult.text = "Testing connection to ${provider.displayName}..."
            binding.btnTestConnection.isEnabled = false

            lifecycleScope.launch {
                val result = apiService.testConnection(provider, apiKey, currentModelId)
                binding.btnTestConnection.isEnabled = true
                if (result.isSuccess) {
                    binding.tvTestConnectionResult.text = "✅ API Connection Successful! Models ready."
                    binding.tvTestConnectionResult.setTextColor(getColor(android.R.color.holo_green_dark))
                } else {
                    binding.tvTestConnectionResult.text = "❌ Connection Failed: ${result.exceptionOrNull()?.localizedMessage}"
                    binding.tvTestConnectionResult.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
        }

        binding.btnSaveSettings.setOnClickListener {
            val provider = getSelectedProvider()
            prefs.activeProvider = provider

            if (provider != AiProvider.OFFLINE_MASTER) {
                val key = binding.etApiKey.text.toString().trim()
                when (provider) {
                    AiProvider.GROQ -> prefs.groqApiKey = key
                    AiProvider.GEMINI -> prefs.geminiApiKey
                    AiProvider.OPENROUTER -> prefs.openrouterApiKey = key
                    AiProvider.OPENAI -> prefs.openaiApiKey = key
                    AiProvider.OFFLINE_MASTER -> {}
                }
                prefs.selectedModel = currentModelId
            }

            Toast.makeText(this, "Settings saved! Active: ${provider.displayName}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getSelectedProvider(): AiProvider {
        return when (binding.rgAiProvider.checkedRadioButtonId) {
            binding.rbGroq.id -> AiProvider.GROQ
            binding.rbGemini.id -> AiProvider.GEMINI
            binding.rbOpenRouter.id -> AiProvider.OPENROUTER
            binding.rbOpenAi.id -> AiProvider.OPENAI
            else -> AiProvider.OFFLINE_MASTER
        }
    }
}
