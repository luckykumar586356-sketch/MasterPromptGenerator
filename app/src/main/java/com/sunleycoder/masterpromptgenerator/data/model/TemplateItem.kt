package com.sunleycoder.masterpromptgenerator.data.model

data class TemplateItem(
    val title: String,
    val idea: String,
    val suggestedStack: String,
    val suggestedFeatures: List<String>,
    val iconEmoji: String
) {
    companion object {
        val ALL = listOf(
            TemplateItem(
                title = "Esports Tournament App",
                idea = "Build a competitive gaming tournament and esports bracket management app with match fixtures, live scores, prize pool, team registrations, and player leaderboards.",
                suggestedStack = "flutter",
                suggestedFeatures = listOf("auth", "tournament", "live_updates", "payments", "chat", "user_profile", "notifications"),
                iconEmoji = "🏆"
            ),
            TemplateItem(
                title = "Full-Stack E-Commerce Store",
                idea = "Develop a high-converting e-commerce shopping app with product catalog, search with multi-filters, cart, checkout, payment gateway, order tracking, and admin dashboard.",
                suggestedStack = "flutter",
                suggestedFeatures = listOf("auth", "payments", "search_filter", "notifications", "admin", "dark_mode", "rest_api"),
                iconEmoji = "🛒"
            ),
            TemplateItem(
                title = "Real-Time Social & Chat",
                idea = "Create a modern social messaging platform with instant private & group chat, media sharing, user stories, push notifications, and online presence indicators.",
                suggestedStack = "react_native",
                suggestedFeatures = listOf("auth", "chat", "live_updates", "file_upload", "user_profile", "notifications", "dark_mode"),
                iconEmoji = "💬"
            ),
            TemplateItem(
                title = "Food Delivery & Tracker",
                idea = "On-demand food delivery app with restaurant menus, cart customization, real-time courier GPS tracking, promo coupons, and order history.",
                suggestedStack = "kotlin_compose",
                suggestedFeatures = listOf("auth", "payments", "live_updates", "search_filter", "notifications", "user_profile"),
                iconEmoji = "🍔"
            ),
            TemplateItem(
                title = "Personal AI Assistant & Chatbot",
                idea = "Intelligent AI assistant application featuring multi-model chat, voice-to-text, markdown rendering, code formatting, chat history search, and customizable system prompts.",
                suggestedStack = "kotlin_compose",
                suggestedFeatures = listOf("ai_ml", "dark_mode", "offline_db", "search_filter", "social_share", "rest_api"),
                iconEmoji = "🤖"
            ),
            TemplateItem(
                title = "Crypto Portfolio & Stocks",
                idea = "Real-time cryptocurrency and stock market tracker with live price charts, watchlists, portfolio profit/loss calculator, and price alerts.",
                suggestedStack = "react_next",
                suggestedFeatures = listOf("live_updates", "analytics", "search_filter", "notifications", "dark_mode", "rest_api"),
                iconEmoji = "📊"
            ),
            TemplateItem(
                title = "Fitness & Workout Tracker",
                idea = "Comprehensive workout and fitness planner with exercise library, set/rep logger, rest timers, weekly progress charts, and offline sync.",
                suggestedStack = "kotlin_compose",
                suggestedFeatures = listOf("offline_db", "analytics", "user_profile", "dark_mode", "notifications"),
                iconEmoji = "🏋️"
            ),
            TemplateItem(
                title = "Interactive Quiz & LMS App",
                idea = "Gamified educational quiz and test preparation app with timed quizzes, leaderboards, subject categories, detailed answer reviews, and offline questions.",
                suggestedStack = "html_single",
                suggestedFeatures = listOf("tournament", "offline_db", "analytics", "user_profile", "dark_mode"),
                iconEmoji = "🎓"
            )
        )
    }
}
