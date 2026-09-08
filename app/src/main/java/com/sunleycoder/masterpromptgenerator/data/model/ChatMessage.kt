package com.sunleycoder.masterpromptgenerator.data.model

import java.io.Serializable
import java.util.UUID

enum class MessageType {
    USER,
    BOT_TEXT,
    BOT_TECH_STACK_CHOICE,
    BOT_FEATURE_CHOICE,
    BOT_GENERATING,
    BOT_FINAL_PROMPT
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: MessageType,
    var text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val appIdea: String = "",
    var selectedTechStack: String? = null,
    val featureIds: MutableList<String> = mutableListOf(),
    var isSubmitted: Boolean = false,
    var promptContent: String? = null
) : Serializable
