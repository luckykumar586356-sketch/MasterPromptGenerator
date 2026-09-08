package com.sunleycoder.masterpromptgenerator.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.local.PromptStorageManager
import com.sunleycoder.masterpromptgenerator.data.model.PromptHistoryItem
import com.sunleycoder.masterpromptgenerator.data.model.TemplateItem
import com.sunleycoder.masterpromptgenerator.databinding.ActivityMainBinding
import com.sunleycoder.masterpromptgenerator.ui.adapter.PromptHistoryAdapter
import com.sunleycoder.masterpromptgenerator.ui.adapter.TemplateAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: AppPreferences
    private lateinit var storage: PromptStorageManager
    private lateinit var historyAdapter: PromptHistoryAdapter
    private var allPrompts: MutableList<PromptHistoryItem> = mutableListOf()
    private var showingFavoritesOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        storage = PromptStorageManager(this)

        setupUI()
        setupTemplates()
        setupHistoryList()
        setupSearchAndTabs()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupUI() {
        binding.tvActiveModelBadge.text = prefs.activeProvider.displayName

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.cardNewPromptHero.setOnClickListener {
            startActivity(Intent(this, PromptWizardActivity::class.java))
        }

        binding.fabNewPrompt.setOnClickListener {
            startActivity(Intent(this, PromptWizardActivity::class.java))
        }
    }

    private fun setupTemplates() {
        binding.rvTemplates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val templateAdapter = TemplateAdapter(TemplateItem.ALL) { template ->
            val intent = Intent(this, PromptWizardActivity::class.java).apply {
                putExtra(PromptWizardActivity.EXTRA_IDEA, template.idea)
                putExtra(PromptWizardActivity.EXTRA_STACK, template.suggestedStack)
                putStringArrayListExtra(PromptWizardActivity.EXTRA_FEATURES, ArrayList(template.suggestedFeatures))
            }
            startActivity(intent)
        }
        binding.rvTemplates.adapter = templateAdapter
    }

    private fun setupHistoryList() {
        binding.rvPromptHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = PromptHistoryAdapter(
            items = mutableListOf(),
            onItemClick = { item ->
                val intent = Intent(this, PromptDetailActivity::class.java).apply {
                    putExtra(PromptDetailActivity.EXTRA_PROMPT_ITEM, item)
                }
                startActivity(intent)
            },
            onFavoriteToggle = { item ->
                storage.toggleFavorite(item.id)
                refreshData()
            },
            onDeleteClick = { item ->
                storage.deletePrompt(item.id)
                refreshData()
            }
        )
        binding.rvPromptHistory.adapter = historyAdapter
    }

    private fun setupSearchAndTabs() {
        binding.tabAllPrompts.setOnClickListener {
            if (showingFavoritesOnly) {
                showingFavoritesOnly = false
                updateTabVisuals()
                filterAndDisplay()
            }
        }

        binding.tabFavorites.setOnClickListener {
            if (!showingFavoritesOnly) {
                showingFavoritesOnly = true
                updateTabVisuals()
                filterAndDisplay()
            }
        }

        binding.etSearchPrompts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplay()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateTabVisuals() {
        if (!showingFavoritesOnly) {
            binding.tabAllPrompts.setBackgroundResource(R.drawable.bg_chip_selected)
            binding.tabAllPrompts.setTextColor(ContextCompat.getColor(this, R.color.chip_selected_text))
            binding.tabFavorites.background = null
            binding.tabFavorites.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        } else {
            binding.tabFavorites.setBackgroundResource(R.drawable.bg_chip_selected)
            binding.tabFavorites.setTextColor(ContextCompat.getColor(this, R.color.chip_selected_text))
            binding.tabAllPrompts.background = null
            binding.tabAllPrompts.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }
    }

    private fun refreshData() {
        allPrompts = storage.getAllPrompts()
        binding.tvActiveModelBadge.text = prefs.activeProvider.displayName
        binding.tvTotalPrompts.text = allPrompts.size.toString()
        binding.tvTotalFavorites.text = allPrompts.count { it.isFavorite }.toString()
        binding.tvEngineStatus.text = if (prefs.getApiKeyForCurrentProvider().isNotBlank() || prefs.activeProvider.name == "OFFLINE_MASTER") "Active" else "Ready"

        filterAndDisplay()
    }

    private fun filterAndDisplay() {
        val query = binding.etSearchPrompts.text.toString().trim().lowercase()
        val filtered = allPrompts.filter { item ->
            val matchesTab = if (showingFavoritesOnly) item.isFavorite else true
            val matchesQuery = query.isEmpty() ||
                    item.title.lowercase().contains(query) ||
                    item.appIdea.lowercase().contains(query) ||
                    item.techStack.lowercase().contains(query) ||
                    item.features.any { it.lowercase().contains(query) }
            matchesTab && matchesQuery
        }

        historyAdapter.updateData(filtered)

        if (filtered.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvPromptHistory.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvPromptHistory.visibility = View.VISIBLE
        }
    }
}
