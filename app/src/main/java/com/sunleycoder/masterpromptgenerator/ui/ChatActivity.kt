package com.sunleycoder.masterpromptgenerator.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.local.ChatSessionStorage
import com.sunleycoder.masterpromptgenerator.data.model.AppFeature
import com.sunleycoder.masterpromptgenerator.data.model.ChatMessage
import com.sunleycoder.masterpromptgenerator.data.model.ChatSession
import com.sunleycoder.masterpromptgenerator.data.model.MessageType
import com.sunleycoder.masterpromptgenerator.data.model.TechStack
import com.sunleycoder.masterpromptgenerator.databinding.ActivityChatBinding
import com.sunleycoder.masterpromptgenerator.generator.AiApiService
import com.sunleycoder.masterpromptgenerator.generator.MasterPromptEngine
import com.sunleycoder.masterpromptgenerator.generator.OfflineMasterEngine
import com.sunleycoder.masterpromptgenerator.ui.adapter.ChatAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_INITIAL_IDEA = "extra_initial_idea"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var prefs: AppPreferences
    private lateinit var sessionStorage: ChatSessionStorage
    private lateinit var chatAdapter: ChatAdapter
    private val apiService = AiApiService()

    private var currentSession: ChatSession = ChatSession()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        sessionStorage = ChatSessionStorage(this)

        setupUI()
        setupChatList()
        setupStarterChips()
        loadOrCreateSession()
    }

    private fun setupUI() {
        binding.btnChatBack.setOnClickListener {
            finish()
        }

        binding.btnChatSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnChatNew.setOnClickListener {
            startNewChat()
        }

        binding.tvChatSubtitle.text = "Active: ${prefs.activeProvider.displayName}"

        binding.btnChatSend.setOnClickListener {
            val text = binding.etChatMessageInput.text.toString().trim()
            if (text.isNotBlank()) {
                handleUserSend(text)
                binding.etChatMessageInput.text.clear()
            }
        }
    }

    private fun setupChatList() {
        chatAdapter = ChatAdapter(
            messages = currentSession.messages,
            onTechSelected = { message, stack ->
                handleTechStackSelected(stack)
            },
            onFeaturesSelected = { message, featureIds ->
                handleFeaturesSelected(featureIds)
            }
        )

        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvChatMessages.layoutManager = layoutManager
        binding.rvChatMessages.adapter = chatAdapter
    }

    private fun setupStarterChips() {
        binding.chipStarterTournament.setOnClickListener {
            handleUserSend("मुझे टूर्नामेंट ऐप के लिए प्रॉम्प्ट लिखो (Esports Bracket & Leaderboard)")
        }

        binding.chipStarterEcommerce.setOnClickListener {
            handleUserSend("Build a Full-Stack E-Commerce Store with Razorpay Payment & Admin Panel")
        }

        binding.chipStarterChat.setOnClickListener {
            handleUserSend("Create a Real-Time Social Chat & Messaging App with WebSockets")
        }

        binding.chipStarterFood.setOnClickListener {
            handleUserSend("Design an On-Demand Food Delivery app with Live GPS Tracking")
        }
    }

    private fun loadOrCreateSession() {
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        val initialIdea = intent.getStringExtra(EXTRA_INITIAL_IDEA)

        if (!sessionId.isNullOrBlank()) {
            val loaded = sessionStorage.getSessionById(sessionId)
            if (loaded != null) {
                currentSession = loaded
                binding.tvChatTitle.text = currentSession.title
                chatAdapter = ChatAdapter(
                    messages = currentSession.messages,
                    onTechSelected = { _, stack -> handleTechStackSelected(stack) },
                    onFeaturesSelected = { _, featIds -> handleFeaturesSelected(featIds) }
                )
                binding.rvChatMessages.adapter = chatAdapter
                binding.scrollStarterChips.visibility = View.GONE
                scrollToBottom()
                return
            }
        }

        // Start fresh
        startNewChat()

        if (!initialIdea.isNullOrBlank()) {
            handleUserSend(initialIdea)
        }
    }

    private fun startNewChat() {
        currentSession = ChatSession(
            title = "New Prompt Chat",
            currentStep = 0
        )

        currentSession.messages.clear()
        val welcomeMsg = ChatMessage(
            type = MessageType.BOT_TEXT,
            text = "👋 **Welcome to Master Prompt Generator!**\n\nDescribe what app or game idea you want to build (e.g. *'मुझे टूर्नामेंट ऐप के लिए प्रॉम्प्ट लिखो'* or *'Build an E-Commerce store'*). I will ask you questions step-by-step and craft a production-grade master prompt for Claude, ChatGPT, and Gemini!"
        )
        currentSession.messages.add(welcomeMsg)

        chatAdapter.notifyDataSetChanged()
        binding.scrollStarterChips.visibility = View.VISIBLE
        binding.tvChatTitle.text = "New Prompt Chat"
        scrollToBottom()
    }

    private fun handleUserSend(text: String) {
        binding.scrollStarterChips.visibility = View.GONE

        // Add user message
        val userMsg = ChatMessage(type = MessageType.USER, text = text)
        currentSession.messages.add(userMsg)
        val userPos = currentSession.messages.size - 1
        chatAdapter.notifyItemInserted(userPos)
        scrollToBottom()

        when (currentSession.currentStep) {
            0 -> {
                // Step 0: User just provided the app idea
                currentSession.appIdea = text
                currentSession.title = generateShortTitle(text)
                binding.tvChatTitle.text = currentSession.title
                currentSession.currentStep = 1

                lifecycleScope.launch {
                    delay(300)
                    // Ask Question 1: Tech Stack
                    val botQuestion1 = ChatMessage(
                        type = MessageType.BOT_TECH_STACK_CHOICE,
                        text = "Awesome! **\"$text\"** badhiya idea hai! Chalo iske liye ek detailed master prompt banate hain.\n\nPehle yeh batao kis technology stack me banayi jayegi?",
                        appIdea = text
                    )
                    currentSession.messages.add(botQuestion1)
                    chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
                    scrollToBottom()
                    saveSession()
                }
            }
            1 -> {
                // User replied with text instead of chip
                val matchedStack = TechStack.ALL.find { text.contains(it.name, ignoreCase = true) || text.contains(it.id, ignoreCase = true) }
                    ?: TechStack.findById("flutter")
                handleTechStackSelected(matchedStack)
            }
            2 -> {
                // User replied with text during features step
                handleFeaturesSelected(listOf("auth", "tournament", "payments", "live_updates", "chat", "user_profile"))
            }
            else -> {
                // Step 3+: Follow-up chat refinement
                handleFollowUpChat(text)
            }
        }
    }

    private fun handleTechStackSelected(stack: TechStack) {
        currentSession.techStack = stack.name
        currentSession.currentStep = 2

        // Add User's selection confirmation bubble
        val userChoiceMsg = ChatMessage(
            type = MessageType.USER,
            text = "I choose ${stack.iconEmoji} **${stack.name}**"
        )
        currentSession.messages.add(userChoiceMsg)
        chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
        scrollToBottom()

        lifecycleScope.launch {
            delay(400)
            // Ask Question 2: Features Multi-select
            val botQuestion2 = ChatMessage(
                type = MessageType.BOT_FEATURE_CHOICE,
                text = "Great! **${stack.name}** kaafi powerful framework hai.\n\nAb yeh batao kaun-kaun se features hone chahiye? (Multi-select options)",
                selectedTechStack = stack.id
            )
            currentSession.messages.add(botQuestion2)
            chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
            scrollToBottom()
            saveSession()
        }
    }

    private fun handleFeaturesSelected(featureIds: List<String>) {
        val allFeats = AppFeature.getDefaultList()
        val featureNames = allFeats.filter { featureIds.contains(it.id) }.map { "${it.emoji} ${it.name}" }
        currentSession.features.clear()
        currentSession.features.addAll(featureNames)
        currentSession.currentStep = 3

        // Add User's selection confirmation bubble
        val userFeatMsg = ChatMessage(
            type = MessageType.USER,
            text = "Selected Features:\n" + featureNames.joinToString("\n• ", prefix = "• ")
        )
        currentSession.messages.add(userFeatMsg)
        chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
        scrollToBottom()

        // Bot Thinking animation
        val thinkingMsg = ChatMessage(
            type = MessageType.BOT_GENERATING,
            text = "Thinking and generating your production master prompt..."
        )
        currentSession.messages.add(thinkingMsg)
        val thinkingIndex = currentSession.messages.size - 1
        chatAdapter.notifyItemInserted(thinkingIndex)
        scrollToBottom()

        lifecycleScope.launch {
            val fullPrompt = generatePromptCore(
                idea = currentSession.appIdea,
                stackName = currentSession.techStack,
                featureIds = featureIds
            )

            currentSession.finalPrompt = fullPrompt

            // Remove thinking message
            if (currentSession.messages.size > thinkingIndex && currentSession.messages[thinkingIndex].type == MessageType.BOT_GENERATING) {
                currentSession.messages.removeAt(thinkingIndex)
                chatAdapter.notifyItemRemoved(thinkingIndex)
            }

            // Add final prompt message
            val finalMsg = ChatMessage(
                type = MessageType.BOT_FINAL_PROMPT,
                text = fullPrompt,
                selectedTechStack = currentSession.techStack,
                promptContent = fullPrompt
            )
            currentSession.messages.add(finalMsg)
            chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
            scrollToBottom()

            // Bot follow-up prompt
            delay(500)
            val followUpOffer = ChatMessage(
                type = MessageType.BOT_TEXT,
                text = "💡 **Your Master Prompt is ready!**\nTap **Copy Prompt** above and paste it into Claude 3.5, ChatGPT, or Cursor.\n\nNeed any adjustments? Just tell me in the chat (e.g. *'Add Firebase Push notification code'* or *'Make it single file HTML'*)."
            )
            currentSession.messages.add(followUpOffer)
            chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
            scrollToBottom()

            saveSession()
        }
    }

    private suspend fun generatePromptCore(idea: String, stackName: String, featureIds: List<String>): String {
        val stack = TechStack.ALL.find { it.name.equals(stackName, ignoreCase = true) } ?: TechStack.findById("flutter")
        val provider = prefs.activeProvider
        val apiKey = prefs.getApiKeyForCurrentProvider()

        if (provider != com.sunleycoder.masterpromptgenerator.data.model.AiProvider.OFFLINE_MASTER && apiKey.isNotBlank()) {
            val userPrompt = """
Create an ultra-detailed, production-ready Master Prompt for:
- App Idea: $idea
- Tech Stack: ${stack.name}
- Selected Features: ${currentSession.features.joinToString(", ")}

Generate the complete master prompt.
""".trimIndent()

            val result = apiService.generatePrompt(
                provider = provider,
                apiKey = apiKey,
                modelName = prefs.selectedModel,
                systemPrompt = MasterPromptEngine.MASTER_SYSTEM_PROMPT,
                userPrompt = userPrompt
            )

            if (result.isSuccess) {
                return result.getOrThrow()
            }
        }

        // Offline Master Engine (Fast & reliable fallback)
        return OfflineMasterEngine.generateMasterPrompt(
            appIdea = idea,
            techStackId = stack.id,
            selectedFeatureIds = featureIds,
            targetAi = "Claude 3.5 Sonnet / ChatGPT 4o / Gemini 1.5 Pro",
            architecture = "Clean Architecture + MVVM Pattern"
        )
    }

    private fun handleFollowUpChat(userText: String) {
        val thinkingMsg = ChatMessage(type = MessageType.BOT_GENERATING, text = "Thinking...")
        currentSession.messages.add(thinkingMsg)
        val thinkingIndex = currentSession.messages.size - 1
        chatAdapter.notifyItemInserted(thinkingIndex)
        scrollToBottom()

        lifecycleScope.launch {
            val provider = prefs.activeProvider
            val apiKey = prefs.getApiKeyForCurrentProvider()

            var replyText: String

            if (provider != com.sunleycoder.masterpromptgenerator.data.model.AiProvider.OFFLINE_MASTER && apiKey.isNotBlank()) {
                val conversationPrompt = """
Current App Idea: ${currentSession.appIdea}
Tech Stack: ${currentSession.techStack}
User follow-up request: $userText

Provide a direct, complete, production-ready solution or prompt refinement for the user's request.
""".trimIndent()

                val result = apiService.generatePrompt(
                    provider = provider,
                    apiKey = apiKey,
                    modelName = prefs.selectedModel,
                    systemPrompt = "You are a Principal Software Architect assisting the user with their app project.",
                    userPrompt = conversationPrompt
                )
                replyText = if (result.isSuccess) result.getOrThrow() else generateOfflineRefinement(userText)
            } else {
                replyText = generateOfflineRefinement(userText)
            }

            if (currentSession.messages.size > thinkingIndex && currentSession.messages[thinkingIndex].type == MessageType.BOT_GENERATING) {
                currentSession.messages.removeAt(thinkingIndex)
                chatAdapter.notifyItemRemoved(thinkingIndex)
            }

            val replyMsg = ChatMessage(type = MessageType.BOT_TEXT, text = replyText)
            currentSession.messages.add(replyMsg)
            chatAdapter.notifyItemInserted(currentSession.messages.size - 1)
            scrollToBottom()
            saveSession()
        }
    }

    private fun generateOfflineRefinement(userQuery: String): String {
        return """
✅ **Update for: "$userQuery"**

Here is the updated architectural specification for your **${currentSession.techStack}** project:

1. **Integrated Component / Logic**:
   - Added dynamic handler for `$userQuery`
   - Bound into `${currentSession.techStack}` dependency tree
   - Strict error boundary & state management integration

2. **Updated Master Directive**:
   When prompting Claude 3.5 Sonnet or ChatGPT 4o, append:
   > *"Also implement fully working production code for $userQuery with complete input validation, error handling, and responsive Material 3 UI."*

You can copy the main prompt above and combine it with this instruction!
""".trimIndent()
    }

    private fun saveSession() {
        sessionStorage.saveSession(currentSession)
    }

    private fun scrollToBottom() {
        if (currentSession.messages.isNotEmpty()) {
            binding.rvChatMessages.post {
                binding.rvChatMessages.smoothScrollToPosition(currentSession.messages.size - 1)
            }
        }
    }

    private fun generateShortTitle(idea: String): String {
        val clean = idea.trim().lines().firstOrNull()?.take(32)?.trim() ?: "Prompt Chat"
        return if (clean.length < idea.length && !clean.endsWith("…")) "$clean…" else clean
    }
}
