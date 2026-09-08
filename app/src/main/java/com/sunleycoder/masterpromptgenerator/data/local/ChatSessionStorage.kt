package com.sunleycoder.masterpromptgenerator.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunleycoder.masterpromptgenerator.data.model.ChatSession
import java.io.File
import java.io.FileWriter

class ChatSessionStorage(context: Context) {
    private val gson = Gson()
    private val sessionFile = File(context.filesDir, "chat_sessions_v1.json")

    @Synchronized
    fun getAllSessions(): MutableList<ChatSession> {
        if (!sessionFile.exists()) return mutableListOf()
        return try {
            val json = sessionFile.readText()
            val type = object : TypeToken<MutableList<ChatSession>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    @Synchronized
    fun getSessionById(id: String): ChatSession? {
        return getAllSessions().find { it.id == id }
    }

    @Synchronized
    fun saveSession(session: ChatSession) {
        val list = getAllSessions()
        val index = list.indexOfFirst { it.id == session.id }
        session.updatedAt = System.currentTimeMillis()
        if (index >= 0) {
            list[index] = session
        } else {
            list.add(0, session)
        }
        writeSessions(list)
    }

    @Synchronized
    fun deleteSession(id: String) {
        val list = getAllSessions()
        list.removeAll { it.id == id }
        writeSessions(list)
    }

    @Synchronized
    fun getTotalSessionsCount(): Int = getAllSessions().size

    private fun writeSessions(list: List<ChatSession>) {
        try {
            val json = gson.toJson(list)
            FileWriter(sessionFile).use { it.write(json) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
