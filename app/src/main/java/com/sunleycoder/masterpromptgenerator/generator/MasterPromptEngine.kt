package com.sunleycoder.masterpromptgenerator.generator

import android.content.Context
import com.sunleycoder.masterpromptgenerator.data.local.AppPreferences
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider
import com.sunleycoder.masterpromptgenerator.data.model.AppFeature
import com.sunleycoder.masterpromptgenerator.data.model.PromptHistoryItem
import com.sunleycoder.masterpromptgenerator.data.model.TechStack

class MasterPromptEngine(context: Context) {
    private val prefs = AppPreferences(context)
    private val apiService = AiApiService()

    companion object {
        const val MASTER_SYSTEM_PROMPT = """You are the World's Premier AI Prompt Engineer and Principal Software Architect.
Your task is to craft an ULTRA-DETAILED, PRODUCTION-GRADE MASTER PROMPT for a user's software/app project.
The prompt you craft will be given to top-tier code LLMs (Claude 3.5 Sonnet, ChatGPT 4o, Gemini 1.5 Pro, Cursor) so they can build the ENTIRE APP from scratch without missing files, placeholders, or bugs.

Your output must be the FINAL READY-TO-USE MASTER PROMPT formatted with:
1. Executive Role & Persona (Senior Staff Architect)
2. Complete Specification & Scope
3. Full Directory Tree & File Architecture
4. Dependencies & Manifest Config
5. Feature-by-Feature Implementation Blueprint
6. UI/UX & Responsive Dark/Light Design Specs
7. Error Handling, Edge Cases, Offline Resilience
8. Direct AI Execution Directive (Forbid placeholders, mandate complete copy-pasteable files)"""
    }

    suspend fun createMasterPrompt(
        appIdea: String,
        techStackId: String,
        selectedFeatureIds: List<String>,
        targetAi: String,
        architecture: String,
        extraRequirements: String
    ): PromptHistoryItem {
        val stack = TechStack.findById(techStackId)
        val allFeatures = AppFeature.getDefaultList()
        val featureNames = allFeatures.filter { selectedFeatureIds.contains(it.id) }.map { it.name }

        var promptContent: String

        val provider = prefs.activeProvider
        val apiKey = prefs.getApiKeyForCurrentProvider()

        if (provider != AiProvider.OFFLINE_MASTER && apiKey.isNotBlank()) {
            val userPrompt = """
Create a Master Prompt for this project:
- App Idea: $appIdea
- Technology Stack: ${stack.name} (${stack.category})
- Selected Features: ${featureNames.joinToString(", ")}
- Target AI: $targetAi
- Architecture: $architecture
- Custom Requirements: $extraRequirements

Generate the complete, exhaustive master prompt now.
""".trimIndent()

            val result = apiService.generatePrompt(
                provider = provider,
                apiKey = apiKey,
                modelName = prefs.selectedModel,
                systemPrompt = MASTER_SYSTEM_PROMPT,
                userPrompt = userPrompt
            )

            promptContent = if (result.isSuccess) {
                result.getOrThrow()
            } else {
                // Graceful fallback to offline engine if API error
                OfflineMasterEngine.generateMasterPrompt(
                    appIdea = appIdea,
                    techStackId = techStackId,
                    selectedFeatureIds = selectedFeatureIds,
                    targetAi = targetAi,
                    architecture = architecture,
                    extraRequirements = extraRequirements
                )
            }
        } else {
            promptContent = OfflineMasterEngine.generateMasterPrompt(
                appIdea = appIdea,
                techStackId = techStackId,
                selectedFeatureIds = selectedFeatureIds,
                targetAi = targetAi,
                architecture = architecture,
                extraRequirements = extraRequirements
            )
        }

        val title = generateCleanTitle(appIdea)

        return PromptHistoryItem(
            title = title,
            appIdea = appIdea,
            techStack = stack.name,
            features = featureNames,
            targetAi = targetAi,
            architecture = architecture,
            fullPrompt = promptContent,
            timestamp = System.currentTimeMillis(),
            isFavorite = false
        )
    }

    private fun generateCleanTitle(idea: String): String {
        val clean = idea.trim().lines().firstOrNull()?.take(40)?.trim() ?: "Custom App Prompt"
        return if (clean.length < idea.trim().length && !clean.endsWith("…")) "$clean…" else clean
    }
}
