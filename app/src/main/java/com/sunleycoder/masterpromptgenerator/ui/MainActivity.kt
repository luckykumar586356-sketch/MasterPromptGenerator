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
import com.sunleycoder.masterpromptgenerator.data.local.ChatSessionStorage
import com.sunleycoder.masterpromptgenerator.data.model.ChatSession
import com.sunleycoder.masterpromptgenerator.data.model.TemplateItem
import com.sunleycoder.masterpromptgenerator.databinding.ActivityMainBinding
import com.sunleycoder.masterpromptgenerator.ui.adapter.ChatSessionAdapter
import com.sunleycoder.masterpromptgenerator.ui.adapter.TemplateAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: AppPreferences
    private lateinit var sessionStorage: ChatSessionStorage
    private lateinit var sessionAdapter: ChatSessionAdapter
    private var allSessions: MutableList<ChatSession> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        sessionStorage = ChatSessionStorage(this)

        setupUI()
        setupTemplates()
        setupSessionsList()
        setupSearch()
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
            startActivity(Intent(this, ChatActivity::class.java))
        }

        binding.fabNewPrompt.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    private fun setupTemplates() {
        binding.rvTemplates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val templateAdapter = TemplateAdapter(TemplateItem.ALL) { template ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_INITIAL_IDEA, template.idea)
            }
            startActivity(intent)
        }
        binding.rvTemplates.adapter = templateAdapter
    }

    private fun setupSessionsList() {
        binding.rvPromptHistory.layoutManager = LinearLayoutManager(this)
        sessionAdapter = ChatSessionAdapter(
            sessions = mutableListOf(),
            onSessionClick = { session ->
                val intent = Intent(this, ChatActivity::class.java).apply {
                    putExtra(ChatActivity.EXTRA_SESSION_ID, session.id)
                }
                startActivity(intent)
            },
            onSessionDelete = { session ->
                sessionStorage.deleteSession(session.id)
                refreshData()
            }
        )
        binding.rvPromptHistory.adapter = sessionAdapter
    }

    private fun setupSearch() {
        binding.etSearchPrompts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplay()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun refreshData() {
        allSessions = sessionStorage.getAllSessions()
        binding.tvActiveModelBadge.text = prefs.activeProvider.displayName
        binding.tvTotalPrompts.text = allSessions.size.toString()
        binding.tvTotalFavorites.text = allSessions.count { it.finalPrompt.isNotBlank() }.toString()
        binding.tvEngineStatus.text = if (prefs.getApiKeyForCurrentProvider().isNotBlank() || prefs.activeProvider.name == "OFFLINE_MASTER") "Active" else "Ready"

        filterAndDisplay()
    }

    private fun filterAndDisplay() {
        val query = binding.etSearchPrompts.text.toString().trim().lowercase()
        val filtered = allSessions.filter { session ->
            query.isEmpty() ||
                    session.title.lowercase().contains(query) ||
                    session.appIdea.lowercase().contains(query) ||
                    session.techStack.lowercase().contains(query) ||
                    session.features.any { it.lowercase().contains(query) }
        }

        sessionAdapter.updateData(filtered)

        if (filtered.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvPromptHistory.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvPromptHistory.visibility = View.VISIBLE
        }
    }
}
