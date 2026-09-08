package com.sunleycoder.masterpromptgenerator.data.local

import android.content.Context
import android.content.SharedPreferences
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("master_prompt_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROVIDER = "key_provider"
        private const val KEY_GROQ_API_KEY = "key_groq_api_key"
        private const val KEY_GEMINI_API_KEY = "key_gemini_api_key"
        private const val KEY_OPENAI_API_KEY = "key_openai_api_key"
        private const val KEY_OPENROUTER_API_KEY = "key_openrouter_api_key"
        private const val KEY_MODEL = "key_model"
        private const val KEY_THEME = "key_theme"
    }

    var activeProvider: AiProvider
        get() {
            val name = prefs.getString(KEY_PROVIDER, AiProvider.OFFLINE_MASTER.name)
            return try {
                AiProvider.valueOf(name ?: AiProvider.OFFLINE_MASTER.name)
            } catch (e: Exception) {
                AiProvider.OFFLINE_MASTER
            }
        }
        set(value) {
            prefs.edit().putString(KEY_PROVIDER, value.name).apply()
        }

    var groqApiKey: String
        get() = prefs.getString(KEY_GROQ_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GROQ_API_KEY, value.trim()).apply()

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_API_KEY, value.trim()).apply()

    var openaiApiKey: String
        get() = prefs.getString(KEY_OPENAI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OPENAI_API_KEY, value.trim()).apply()

    var openrouterApiKey: String
        get() = prefs.getString(KEY_OPENROUTER_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OPENROUTER_API_KEY, value.trim()).apply()

    var selectedModel: String
        get() = prefs.getString(KEY_MODEL, "llama-3.3-70b-versatile") ?: "llama-3.3-70b-versatile"
        set(value) = prefs.edit().putString(KEY_MODEL, value).apply()

    fun getApiKeyForCurrentProvider(): String {
        return when (activeProvider) {
            AiProvider.GROQ -> groqApiKey
            AiProvider.GEMINI -> geminiApiKey
            AiProvider.OPENAI -> openaiApiKey
            AiProvider.OPENROUTER -> openrouterApiKey
            AiProvider.OFFLINE_MASTER -> ""
        }
    }
}
