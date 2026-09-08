package com.sunleycoder.masterpromptgenerator.data.model

import java.io.Serializable
import java.util.UUID

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "New Prompt Chat",
    val messages: MutableList<ChatMessage> = mutableListOf(),
    var appIdea: String = "",
    var techStack: String = "",
    val features: MutableList<String> = mutableListOf(),
    var finalPrompt: String = "",
    var currentStep: Int = 0, // 0: Init, 1: Tech Stack, 2: Features, 3: Completed
    var updatedAt: Long = System.currentTimeMillis()
) : Serializable
