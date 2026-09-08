package com.sunleycoder.masterpromptgenerator.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider
import com.sunleycoder.masterpromptgenerator.generator.AiApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiTesterDialog(
    context: Context,
    private val masterPrompt: String
) : Dialog(context) {

    private val prefs = AppPreferences(context)
    private val apiService = AiApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_ai_tester)

        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tvOutput = findViewById<TextView>(R.id.tvTesterOutput)
        val pbLoading = findViewById<ProgressBar>(R.id.pbTesterLoading)
        val btnRun = findViewById<MaterialButton>(R.id.btnTesterRun)
        val btnClose = findViewById<MaterialButton>(R.id.btnTesterClose)

        btnClose.setOnClickListener {
            dismiss()
        }

        btnRun.setOnClickListener {
            val provider = prefs.activeProvider
            val apiKey = prefs.getApiKeyForCurrentProvider()

            if (provider == AiProvider.OFFLINE_MASTER) {
                tvOutput.text = "⚡ AI Test Mode:\nCurrently using Built-in Offline Engine.\nTo test real-time code generation with an online LLM, configure a free Groq or Gemini API key in Settings!"
                return@setOnClickListener
            }

            if (apiKey.isBlank()) {
                tvOutput.text = "⚠️ API Key Missing:\nPlease add your ${provider.displayName} API key in Settings to run live tests."
                return@setOnClickListener
            }

            pbLoading.visibility = View.VISIBLE
            btnRun.isEnabled = false
            tvOutput.text = "Connecting to ${provider.displayName}...\nSending Master Prompt..."

            CoroutineScope(Dispatchers.Main).launch {
                val result = apiService.generatePrompt(
                    provider = provider,
                    apiKey = apiKey,
                    modelName = prefs.selectedModel,
                    systemPrompt = "You are a senior full-stack developer. Write the initial complete code based on this master prompt.",
                    userPrompt = masterPrompt.take(3000)
                )

                pbLoading.visibility = View.GONE
                btnRun.isEnabled = true

                if (result.isSuccess) {
                    tvOutput.text = result.getOrNull()
                } else {
                    tvOutput.text = "❌ Error during AI Test:\n${result.exceptionOrNull()?.localizedMessage}"
                }
            }
        }
    }
}
