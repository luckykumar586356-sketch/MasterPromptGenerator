package com.sunleycoder.masterpromptgenerator.data.model

import java.io.Serializable
import java.util.UUID

data class PromptHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    val appIdea: String,
    val techStack: String,
    val features: List<String>,
    val targetAi: String = "Claude 3.5 Sonnet / ChatGPT 4o",
    val architecture: String = "MVVM + Clean Architecture",
    var fullPrompt: String,
    val timestamp: Long = System.currentTimeMillis(),
    var isFavorite: Boolean = false
) : Serializable
