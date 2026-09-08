package com.sunleycoder.masterpromptgenerator.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.local.PromptStorageManager
import com.sunleycoder.masterpromptgenerator.data.model.AppFeature
import com.sunleycoder.masterpromptgenerator.data.model.TechStack
import com.sunleycoder.masterpromptgenerator.databinding.ActivityPromptWizardBinding
import com.sunleycoder.masterpromptgenerator.generator.MasterPromptEngine
import com.sunleycoder.masterpromptgenerator.ui.adapter.FeatureChipAdapter
import com.sunleycoder.masterpromptgenerator.ui.adapter.TechStackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PromptWizardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IDEA = "extra_idea"
        const val EXTRA_STACK = "extra_stack"
        const val EXTRA_FEATURES = "extra_features"
    }

    private lateinit var binding: ActivityPromptWizardBinding
    private lateinit var techStackAdapter: TechStackAdapter
    private lateinit var featureChipAdapter: FeatureChipAdapter
    private lateinit var prefs: AppPreferences
    private lateinit var storage: PromptStorageManager
    private lateinit var engine: MasterPromptEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromptWizardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        storage = PromptStorageManager(this)
        engine = MasterPromptEngine(this)

        setupToolbar()
        setupTechStacks()
        setupFeatures()
        setupSpinners()
        setupGenerateButton()
        handleIncomingData()
    }

    private fun setupToolbar() {
        binding.btnWizardBack.setOnClickListener {
            finish()
        }
        binding.tvWizardProviderBadge.text = prefs.activeProvider.displayName
    }

    private fun setupTechStacks() {
        binding.rvTechStacks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        techStackAdapter = TechStackAdapter(TechStack.ALL, "flutter") { selected ->
            // Stack selected
        }
        binding.rvTechStacks.adapter = techStackAdapter
    }

    private fun setupFeatures() {
        val features = AppFeature.getDefaultList()
        binding.rvFeatures.layoutManager = GridLayoutManager(this, 2)
        featureChipAdapter = FeatureChipAdapter(features) { selectedIds ->
            binding.tvFeaturesCountLabel.text = "${selectedIds.size} features selected"
        }
        binding.rvFeatures.adapter = featureChipAdapter

        binding.btnSelectAllFeatures.setOnClickListener {
            featureChipAdapter.selectAll()
        }

        binding.btnClearFeatures.setOnClickListener {
            featureChipAdapter.clearAll()
        }
    }

    private fun setupSpinners() {
        val targetAis = listOf(
            "Claude 3.5 Sonnet (Anthropic)",
            "ChatGPT 4o (OpenAI)",
            "Google Gemini 1.5 Pro",
            "Cursor AI / Windsurf Composer",
            "DeepSeek V3 / R1"
        )
        val targetAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, targetAis)
        binding.spinnerTargetAi.adapter = targetAdapter

        val architectures = listOf(
            "MVVM + Clean Architecture (Repository Pattern)",
            "Single File Self-Contained (Zero Build Setup)",
            "MVI / Unidirectional State Flow",
            "Domain Driven Design (Modular Components)"
        )
        val archAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, architectures)
        binding.spinnerArchitecture.adapter = archAdapter
    }

    private fun handleIncomingData() {
        val prefilledIdea = intent.getStringExtra(EXTRA_IDEA)
        if (!prefilledIdea.isNullOrBlank()) {
            binding.etAppIdea.setText(prefilledIdea)
        }

        val prefilledStack = intent.getStringExtra(EXTRA_STACK)
        if (!prefilledStack.isNullOrBlank()) {
            techStackAdapter.setSelectedStack(prefilledStack)
        }

        val prefilledFeatures = intent.getStringArrayListExtra(EXTRA_FEATURES)
        if (!prefilledFeatures.isNullOrEmpty()) {
            featureChipAdapter.setSelectedIds(prefilledFeatures)
        }
    }

    private fun setupGenerateButton() {
        binding.btnGenerateMasterPrompt.setOnClickListener {
            val idea = binding.etAppIdea.text.toString().trim()
            if (idea.isBlank()) {
                Toast.makeText(this, "Please describe your app or game idea first!", Toast.LENGTH_SHORT).show()
                binding.etAppIdea.requestFocus()
                return@setOnClickListener
            }

            val stack = techStackAdapter.getSelectedStack()
            val selectedFeatureIds = featureChipAdapter.getSelectedFeatureIds()
            val targetAi = binding.spinnerTargetAi.selectedItem?.toString() ?: "Claude 3.5 Sonnet"
            val architecture = binding.spinnerArchitecture.selectedItem?.toString() ?: "Clean Architecture + MVVM"
            val extraNotes = binding.etExtraNotes.text.toString().trim()

            binding.layoutGenerationProgress.visibility = View.VISIBLE
            binding.btnGenerateMasterPrompt.isEnabled = false

            lifecycleScope.launch {
                binding.tvProgressStatus.text = "Architecting system models & directory tree..."
                delay(400)
                binding.tvProgressStatus.text = "Synthesizing dependencies & feature specifications..."

                try {
                    val promptItem = engine.createMasterPrompt(
                        appIdea = idea,
                        techStackId = stack.id,
                        selectedFeatureIds = selectedFeatureIds,
                        targetAi = targetAi,
                        architecture = architecture,
                        extraRequirements = extraNotes
                    )

                    storage.savePrompt(promptItem)

                    binding.layoutGenerationProgress.visibility = View.GONE
                    binding.btnGenerateMasterPrompt.isEnabled = true

                    val intent = Intent(this@PromptWizardActivity, PromptDetailActivity::class.java).apply {
                        putExtra(PromptDetailActivity.EXTRA_PROMPT_ITEM, promptItem)
                    }
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    binding.layoutGenerationProgress.visibility = View.GONE
                    binding.btnGenerateMasterPrompt.isEnabled = true
                    Toast.makeText(this@PromptWizardActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
