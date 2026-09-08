package com.sunleycoder.masterpromptgenerator.data.model

enum class AiProvider(val displayName: String) {
    OFFLINE_MASTER("Built-in Offline Master Engine"),
    GROQ("Groq Console (Ultra-Fast)"),
    GEMINI("Google Gemini (AI Studio)"),
    OPENAI("OpenAI (ChatGPT)"),
    OPENROUTER("OpenRouter")
}

data class AiModelOption(
    val provider: AiProvider,
    val modelId: String,
    val displayName: String
) {
    companion object {
        val ALL = listOf(
            AiModelOption(AiProvider.OFFLINE_MASTER, "offline_engine", "Offline Master Architect (No Key Needed)"),
            AiModelOption(AiProvider.GROQ, "llama-3.3-70b-versatile", "Groq: Llama 3.3 70B (Recommended)"),
            AiModelOption(AiProvider.GROQ, "llama-3.1-8b-instant", "Groq: Llama 3.1 8B Instant"),
            AiModelOption(AiProvider.GROQ, "mixtral-8x7b-32768", "Groq: Mixtral 8x7B"),
            AiModelOption(AiProvider.GEMINI, "gemini-1.5-flash", "Gemini 1.5 Flash"),
            AiModelOption(AiProvider.GEMINI, "gemini-1.5-pro", "Gemini 1.5 Pro"),
            AiModelOption(AiProvider.OPENAI, "gpt-4o", "OpenAI: GPT-4o"),
            AiModelOption(AiProvider.OPENAI, "gpt-4o-mini", "OpenAI: GPT-4o Mini"),
            AiModelOption(AiProvider.OPENROUTER, "meta-llama/llama-3.3-70b-instruct:free", "OpenRouter: Llama 3.3 70B (Free)")
        )
    }
}
