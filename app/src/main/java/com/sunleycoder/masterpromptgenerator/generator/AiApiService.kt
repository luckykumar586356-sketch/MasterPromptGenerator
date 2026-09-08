package com.sunleycoder.masterpromptgenerator.generator

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class AiApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generatePrompt(
        provider: AiProvider,
        apiKey: String,
        modelName: String,
        systemPrompt: String,
        userPrompt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            when (provider) {
                AiProvider.GROQ -> callOpenAiCompatible(
                    endpoint = "https://api.groq.com/openai/v1/chat/completions",
                    apiKey = apiKey,
                    model = modelName.ifBlank { "llama-3.3-70b-versatile" },
                    systemPrompt = systemPrompt,
                    userPrompt = userPrompt
                )
                AiProvider.OPENAI -> callOpenAiCompatible(
                    endpoint = "https://api.openai.com/v1/chat/completions",
                    apiKey = apiKey,
                    model = modelName.ifBlank { "gpt-4o" },
                    systemPrompt = systemPrompt,
                    userPrompt = userPrompt
                )
                AiProvider.OPENROUTER -> callOpenAiCompatible(
                    endpoint = "https://openrouter.ai/api/v1/chat/completions",
                    apiKey = apiKey,
                    model = modelName.ifBlank { "meta-llama/llama-3.3-70b-instruct:free" },
                    systemPrompt = systemPrompt,
                    userPrompt = userPrompt,
                    extraHeaders = mapOf(
                        "HTTP-Referer" to "https://sunleycoder.com",
                        "X-Title" to "Master Prompt Generator"
                    )
                )
                AiProvider.GEMINI -> callGemini(
                    apiKey = apiKey,
                    model = modelName.ifBlank { "gemini-1.5-flash" },
                    systemPrompt = systemPrompt,
                    userPrompt = userPrompt
                )
                AiProvider.OFFLINE_MASTER -> {
                    Result.failure(IllegalStateException("Offline engine does not require network API call."))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(provider: AiProvider, apiKey: String, model: String): Result<String> = withContext(Dispatchers.IO) {
        if (provider == AiProvider.OFFLINE_MASTER) {
            return@withContext Result.success("Offline Master Engine is always active and ready!")
        }
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid API key first."))
        }
        generatePrompt(
            provider = provider,
            apiKey = apiKey,
            modelName = model,
            systemPrompt = "Respond with 'API Connection Successful' only.",
            userPrompt = "Ping"
        )
    }

    private fun callOpenAiCompatible(
        endpoint: String,
        apiKey: String,
        model: String,
        systemPrompt: String,
        userPrompt: String,
        extraHeaders: Map<String, String> = emptyMap()
    ): Result<String> {
        val root = JsonObject().apply {
            addProperty("model", model)
            val messages = com.google.gson.JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("role", "system")
                    addProperty("content", systemPrompt)
                })
                add(JsonObject().apply {
                    addProperty("role", "user")
                    addProperty("content", userPrompt)
                })
            }
            add("messages", messages)
            addProperty("temperature", 0.7)
            addProperty("max_tokens", 4096)
        }

        val requestBuilder = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(root.toString().toRequestBody(jsonMediaType))

        extraHeaders.forEach { (k, v) -> requestBuilder.addHeader(k, v) }

        val response = client.newCall(requestBuilder.build()).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            return Result.failure(Exception("API Error (${response.code}): $responseBody"))
        }

        return try {
            val json = gson.fromJson(responseBody, JsonObject::class.java)
            val choices = json.getAsJsonArray("choices")
            if (choices != null && choices.size() > 0) {
                val message = choices.get(0).asJsonObject.getAsJsonObject("message")
                val content = message.get("content").asString
                Result.success(content)
            } else {
                Result.failure(Exception("Empty choices received from AI service."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to parse API response: ${e.message}"))
        }
    }

    private fun callGemini(
        apiKey: String,
        model: String,
        systemPrompt: String,
        userPrompt: String
    ): Result<String> {
        val cleanModel = if (model.startsWith("models/")) model.removePrefix("models/") else model
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$cleanModel:generateContent?key=$apiKey"

        val root = JsonObject().apply {
            val contents = com.google.gson.JsonArray().apply {
                add(JsonObject().apply {
                    val parts = com.google.gson.JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("text", "$systemPrompt\n\nTask:\n$userPrompt")
                        })
                    }
                    add("parts", parts)
                })
            }
            add("contents", contents)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(root.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            return Result.failure(Exception("Gemini API Error (${response.code}): $responseBody"))
        }

        return try {
            val json = gson.fromJson(responseBody, JsonObject::class.java)
            val candidates = json.getAsJsonArray("candidates")
            if (candidates != null && candidates.size() > 0) {
                val content = candidates.get(0).asJsonObject.getAsJsonObject("content")
                val parts = content.getAsJsonArray("parts")
                val text = parts.get(0).asJsonObject.get("text").asString
                Result.success(text)
            } else {
                Result.failure(Exception("Empty candidates received from Gemini."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to parse Gemini response: ${e.message}"))
        }
    }
}
