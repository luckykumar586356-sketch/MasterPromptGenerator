package com.sunleycoder.masterpromptgenerator.data.model

import java.io.Serializable

enum class AiProvider(
    val displayName: String,
    val keyUrl: String,
    val isFreeAvailable: Boolean,
    val helpGuideHindi: String
) {
    OFFLINE_MASTER(
        displayName = "Built-in Offline Master Engine",
        keyUrl = "",
        isFreeAvailable = true,
        helpGuideHindi = "100% Offline & Free! Iske liye kisi internet ya API key ki zarurat nahi hai. Yeh direct device par high-quality prompt generate karta hai."
    ),
    GROQ(
        displayName = "Groq Console (Ultra-Fast Llama 3.3)",
        keyUrl = "https://console.groq.com/keys",
        isFreeAvailable = true,
        helpGuideHindi = "1. console.groq.com/keys par jayein\n2. Google account se login karein (Credit card nahi chahiye)\n3. 'Create API Key' par click karein\n4. Key copy karke yahan paste karein.\n\n✨ 100% FREE Tier & Superfast Speed (300+ tokens/sec)!"
    ),
    GEMINI(
        displayName = "Google Gemini (AI Studio)",
        keyUrl = "https://aistudio.google.com/app/apikey",
        isFreeAvailable = true,
        helpGuideHindi = "1. aistudio.google.com/app/apikey par jayein\n2. Google account se sign in karein\n3. 'Create API key' button par click karein\n4. Key copy karke yahan paste karein.\n\n✨ Free tier me 15 Requests/min aur 1 Million token context milta hai!"
    ),
    OPENROUTER(
        displayName = "OpenRouter (Multi-Model Free & Paid)",
        keyUrl = "https://openrouter.ai/keys",
        isFreeAvailable = true,
        helpGuideHindi = "1. openrouter.ai/keys par jayein\n2. Google ya GitHub se sign in karein\n3. 'Create Key' par click karein\n4. Yahan paste karein.\n\n✨ Isme ':free' wale sabhi models bina kisi recharge ke bilkul FREE chalte hain!"
    ),
    OPENAI(
        displayName = "OpenAI (ChatGPT GPT-4o)",
        keyUrl = "https://platform.openai.com/api-keys",
        isFreeAvailable = false,
        helpGuideHindi = "1. platform.openai.com/api-keys par jayein\n2. Account login karein\n3. 'Create new secret key' par click karein.\n\n⚠️ OpenAI me trial credits ya account balance hona zaroori hai."
    )
}

data class ModelInfo(
    val provider: AiProvider,
    val modelId: String,
    val displayName: String,
    val isFree: Boolean,
    val tierLabel: String, // "100% FREE", "FREE TIER", "PAID / CREDITS"
    val description: String,
    val contextWindow: String = "128k context"
) : Serializable {
    companion object {
        fun getCuratedModels(provider: AiProvider): List<ModelInfo> {
            return when (provider) {
                AiProvider.OFFLINE_MASTER -> listOf(
                    ModelInfo(
                        provider = AiProvider.OFFLINE_MASTER,
                        modelId = "offline_engine",
                        displayName = "Offline Master Architect Engine",
                        isFree = true,
                        tierLabel = "100% FREE (OFFLINE)",
                        description = "Instant generation directly on device. Zero data usage, zero latency, highly detailed prompt output.",
                        contextWindow = "Unlimited Local"
                    )
                )

                AiProvider.GROQ -> listOf(
                    ModelInfo(
                        provider = AiProvider.GROQ,
                        modelId = "llama-3.3-70b-versatile",
                        displayName = "Llama 3.3 70B Versatile",
                        isFree = true,
                        tierLabel = "🟢 100% FREE TIER",
                        description = "State-of-the-art 70B parameter model. Best reasoning for architecture and complete app coding.",
                        contextWindow = "128k context • Ultra-Fast"
                    ),
                    ModelInfo(
                        provider = AiProvider.GROQ,
                        modelId = "llama-3.1-8b-instant",
                        displayName = "Llama 3.1 8B Instant",
                        isFree = true,
                        tierLabel = "🟢 100% FREE TIER",
                        description = "Lightning-fast speed (>600 tokens/sec). Great for instant prompt drafting and quick edits.",
                        contextWindow = "128k context • Fastest"
                    ),
                    ModelInfo(
                        provider = AiProvider.GROQ,
                        modelId = "mixtral-8x7b-32768",
                        displayName = "Mixtral 8x7B (MoE)",
                        isFree = true,
                        tierLabel = "🟢 100% FREE TIER",
                        description = "High-efficiency Mixture of Experts model with deep technical and algorithmic coding depth.",
                        contextWindow = "32k context"
                    ),
                    ModelInfo(
                        provider = AiProvider.GROQ,
                        modelId = "gemma2-9b-it",
                        displayName = "Google Gemma 2 9B",
                        isFree = true,
                        tierLabel = "🟢 100% FREE TIER",
                        description = "Google's high-efficiency lightweight model tuned for clean instructions and prompt logic.",
                        contextWindow = "8k context"
                    )
                )

                AiProvider.GEMINI -> listOf(
                    ModelInfo(
                        provider = AiProvider.GEMINI,
                        modelId = "gemini-1.5-flash",
                        displayName = "Gemini 1.5 Flash",
                        isFree = true,
                        tierLabel = "🟢 FREE TIER (15 RPM)",
                        description = "Google's fastest multimodal model. Free tier includes generous 15 requests per minute.",
                        contextWindow = "1 Million tokens"
                    ),
                    ModelInfo(
                        provider = AiProvider.GEMINI,
                        modelId = "gemini-1.5-pro",
                        displayName = "Gemini 1.5 Pro",
                        isFree = true,
                        tierLabel = "🟢 FREE TIER (2 RPM)",
                        description = "Google's flagship reasoning engine. Ideal for complex multi-module architecture planning.",
                        contextWindow = "2 Million tokens"
                    ),
                    ModelInfo(
                        provider = AiProvider.GEMINI,
                        modelId = "gemini-2.0-flash-exp",
                        displayName = "Gemini 2.0 Flash (Experimental)",
                        isFree = true,
                        tierLabel = "🟢 FREE PREVIEW",
                        description = "Next-generation ultra-fast Gemini 2.0 architecture with superior code synthesis.",
                        contextWindow = "1 Million tokens"
                    )
                )

                AiProvider.OPENROUTER -> listOf(
                    ModelInfo(
                        provider = AiProvider.OPENROUTER,
                        modelId = "meta-llama/llama-3.3-70b-instruct:free",
                        displayName = "Llama 3.3 70B (OpenRouter Free)",
                        isFree = true,
                        tierLabel = "🟢 100% FREE",
                        description = "Full 70B open weights model hosted free on OpenRouter.",
                        contextWindow = "128k context"
                    ),
                    ModelInfo(
                        provider = AiProvider.OPENROUTER,
                        modelId = "google/gemini-2.0-flash-exp:free",
                        displayName = "Gemini 2.0 Flash (Free)",
                        isFree = true,
                        tierLabel = "🟢 100% FREE",
                        description = "Google's Gemini 2.0 routed free via OpenRouter gateway.",
                        contextWindow = "1 Million tokens"
                    ),
                    ModelInfo(
                        provider = AiProvider.OPENROUTER,
                        modelId = "deepseek/deepseek-r1:free",
                        displayName = "DeepSeek R1 (Free)",
                        isFree = true,
                        tierLabel = "🟢 100% FREE",
                        description = "Revolutionary open reasoning model with step-by-step thinking for code generation.",
                        contextWindow = "64k context"
                    ),
                    ModelInfo(
                        provider = AiProvider.OPENROUTER,
                        modelId = "anthropic/claude-3.5-sonnet",
                        displayName = "Claude 3.5 Sonnet",
                        isFree = false,
                        tierLabel = "🔵 PAID / CREDITS",
                        description = "Industry top coding model. Requires OpenRouter balance.",
                        contextWindow = "200k context"
                    )
                )

                AiProvider.OPENAI -> listOf(
                    ModelInfo(
                        provider = AiProvider.OPENAI,
                        modelId = "gpt-4o",
                        displayName = "OpenAI GPT-4o (Flagship)",
                        isFree = false,
                        tierLabel = "🔵 PAID / CREDITS",
                        description = "Omni-model by OpenAI. High coding accuracy, requires OpenAI billing credits.",
                        contextWindow = "128k context"
                    ),
                    ModelInfo(
                        provider = AiProvider.OPENAI,
                        modelId = "gpt-4o-mini",
                        displayName = "OpenAI GPT-4o Mini",
                        isFree = false,
                        tierLabel = "🔵 LOW-COST PAID",
                        description = "Affordable, lightweight GPT-4o variant with quick responses.",
                        contextWindow = "128k context"
                    )
                )
            }
        }
    }
}
