package com.sunleycoder.masterpromptgenerator.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider
import com.sunleycoder.masterpromptgenerator.databinding.ActivitySettingsBinding
import com.sunleycoder.masterpromptgenerator.generator.AiApiService
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: AppPreferences
    private val apiService = AiApiService()

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
            AiProvider.OPENAI -> binding.rbOpenAi.isChecked = true
            AiProvider.OPENROUTER -> binding.rbOpenRouter.isChecked = true
        }

        updateProviderView(prefs.activeProvider)
    }

    private fun updateProviderView(provider: AiProvider) {
        if (provider == AiProvider.OFFLINE_MASTER) {
            binding.cardApiConfig.visibility = View.GONE
        } else {
            binding.cardApiConfig.visibility = View.VISIBLE
            val currentKey = when (provider) {
                AiProvider.GROQ -> prefs.groqApiKey
                AiProvider.GEMINI -> prefs.geminiApiKey
                AiProvider.OPENAI -> prefs.openaiApiKey
                AiProvider.OPENROUTER -> prefs.openrouterApiKey
                AiProvider.OFFLINE_MASTER -> ""
            }
            binding.etApiKey.setText(currentKey)
            binding.tvApiKeyLabel.text = "${provider.displayName} API Key:"

            val defaultModel = when (provider) {
                AiProvider.GROQ -> "llama-3.3-70b-versatile"
                AiProvider.GEMINI -> "gemini-1.5-flash"
                AiProvider.OPENAI -> "gpt-4o"
                AiProvider.OPENROUTER -> "meta-llama/llama-3.3-70b-instruct:free"
                AiProvider.OFFLINE_MASTER -> "offline_engine"
            }
            val storedModel = prefs.selectedModel
            binding.etModelName.setText(if (storedModel.isNotBlank()) storedModel else defaultModel)
        }
        binding.tvTestConnectionResult.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.rgAiProvider.setOnCheckedChangeListener { _, checkedId ->
            val provider = when (checkedId) {
                binding.rbOfflineMaster.id -> AiProvider.OFFLINE_MASTER
                binding.rbGroq.id -> AiProvider.GROQ
                binding.rbGemini.id -> AiProvider.GEMINI
                binding.rbOpenAi.id -> AiProvider.OPENAI
                binding.rbOpenRouter.id -> AiProvider.OPENROUTER
                else -> AiProvider.OFFLINE_MASTER
            }
            updateProviderView(provider)
        }

        binding.btnTestConnection.setOnClickListener {
            val provider = getSelectedProvider()
            val apiKey = binding.etApiKey.text.toString().trim()
            val model = binding.etModelName.text.toString().trim()

            binding.tvTestConnectionResult.visibility = View.VISIBLE
            binding.tvTestConnectionResult.text = "Testing connection to ${provider.displayName}..."
            binding.btnTestConnection.isEnabled = false

            lifecycleScope.launch {
                val result = apiService.testConnection(provider, apiKey, model)
                binding.btnTestConnection.isEnabled = true
                if (result.isSuccess) {
                    binding.tvTestConnectionResult.text = "✅ Connection Successful!"
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
                val model = binding.etModelName.text.toString().trim()

                when (provider) {
                    AiProvider.GROQ -> prefs.groqApiKey = key
                    AiProvider.GEMINI -> prefs.geminiApiKey = key
                    AiProvider.OPENAI -> prefs.openaiApiKey = key
                    AiProvider.OPENROUTER -> prefs.openrouterApiKey = key
                    AiProvider.OFFLINE_MASTER -> {}
                }
                prefs.selectedModel = model
            }

            Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getSelectedProvider(): AiProvider {
        return when (binding.rgAiProvider.checkedRadioButtonId) {
            binding.rbGroq.id -> AiProvider.GROQ
            binding.rbGemini.id -> AiProvider.GEMINI
            binding.rbOpenAi.id -> AiProvider.OPENAI
            binding.rbOpenRouter.id -> AiProvider.OPENROUTER
            else -> AiProvider.OFFLINE_MASTER
        }
    }
}
