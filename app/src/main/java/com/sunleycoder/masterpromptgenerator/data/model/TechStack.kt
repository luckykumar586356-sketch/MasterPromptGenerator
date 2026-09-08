package com.sunleycoder.masterpromptgenerator.data.model

data class TechStack(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val category: String,
    val defaultStructure: String
) {
    companion object {
        val ALL = listOf(
            TechStack("flutter", "Flutter (Dart)", "📱", "Cross-Platform", "lib/ (main.dart, models/, views/, controllers/, services/)"),
            TechStack("kotlin_compose", "Kotlin Android (Jetpack Compose)", "🤖", "Native Android", "app/src/main/java/com/example/app/ (ui/, data/, domain/)"),
            TechStack("kotlin_xml", "Kotlin Android (XML Views)", "📱", "Native Android", "app/src/main/java/com/example/app/ (activities/, adapters/, viewmodel/)"),
            TechStack("react_native", "React Native (Expo / TypeScript)", "⚛️", "Cross-Platform", "src/ (components/, screens/, navigation/, hooks/, api/)"),
            TechStack("html_single", "HTML / CSS / JavaScript (Single File)", "🌐", "Web", "Single index.html with embedded <style> and <script>"),
            TechStack("react_next", "Next.js / React (TypeScript + Tailwind)", "🚀", "Full-Stack Web", "app/ (api/, components/, lib/, styles/)"),
            TechStack("python_fastapi", "Python (FastAPI + Streamlit / UI)", "🐍", "Backend / AI", "main.py, routers/, services/, models/, config/"),
            TechStack("java_android", "Java Android", "☕", "Native Android", "app/src/main/java/ (activities/, fragments/, models/, db/)"),
            TechStack("node_express", "Node.js (Express + TypeScript)", "🟢", "Backend API", "src/ (controllers/, routes/, middlewares/, models/, services/)"),
            TechStack("swift_ios", "Swift (SwiftUI)", "🍏", "Native iOS", "Sources/ (Views/, ViewModels/, Models/, Services/)")
        )

        fun findById(id: String): TechStack {
            return ALL.find { it.id.equals(id, ignoreCase = true) } ?: ALL[0]
        }
    }
}
