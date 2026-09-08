package com.sunleycoder.masterpromptgenerator.data.local

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunleycoder.masterpromptgenerator.data.model.PromptHistoryItem
import java.io.File
import java.io.FileWriter

class PromptStorageManager(private val context: Context) {
    private val gson = Gson()
    private val historyFile: File = File(context.filesDir, "master_prompts_history.json")

    @Synchronized
    fun getAllPrompts(): MutableList<PromptHistoryItem> {
        if (!historyFile.exists()) return mutableListOf()
        return try {
            val json = historyFile.readText()
            val type = object : TypeToken<MutableList<PromptHistoryItem>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    @Synchronized
    fun savePrompt(item: PromptHistoryItem) {
        val list = getAllPrompts()
        val index = list.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            list[index] = item
        } else {
            list.add(0, item) // Add latest at top
        }
        writeList(list)
    }

    @Synchronized
    fun deletePrompt(id: String) {
        val list = getAllPrompts()
        list.removeAll { it.id == id }
        writeList(list)
    }

    @Synchronized
    fun toggleFavorite(id: String): Boolean {
        val list = getAllPrompts()
        val item = list.find { it.id == id } ?: return false
        item.isFavorite = !item.isFavorite
        writeList(list)
        return item.isFavorite
    }

    @Synchronized
    fun getFavoritesCount(): Int {
        return getAllPrompts().count { it.isFavorite }
    }

    @Synchronized
    fun getTotalCount(): Int {
        return getAllPrompts().size
    }

    private fun writeList(list: List<PromptHistoryItem>) {
        try {
            val json = gson.toJson(list)
            FileWriter(historyFile).use { it.write(json) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exportToDownloads(item: PromptHistoryItem): File? {
        return try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) downloadDir.mkdirs()
            val sanitized = item.title.replace("[^a-zA-Z0-9_-]".toRegex(), "_").take(30)
            val file = File(downloadDir, "MasterPrompt_${sanitized}_${System.currentTimeMillis()}.md")
            FileWriter(file).use { writer ->
                writer.write("# Master Prompt: ${item.title}\n\n")
                writer.write("- **Tech Stack**: ${item.techStack}\n")
                writer.write("- **Target AI**: ${item.targetAi}\n")
                writer.write("- **Architecture**: ${item.architecture}\n")
                writer.write("- **Features**: ${item.features.joinToString(", ")}\n\n")
                writer.write("---\n\n")
                writer.write(item.fullPrompt)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
