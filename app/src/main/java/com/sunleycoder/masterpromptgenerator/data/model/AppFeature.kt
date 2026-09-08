package com.sunleycoder.masterpromptgenerator.data.model

data class AppFeature(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val description: String,
    var isSelected: Boolean = false
) {
    companion object {
        fun getDefaultList(): List<AppFeature> {
            return listOf(
                AppFeature("auth", "Login & Sign Up", "🔐", "Security", "Email, Password, OAuth (Google/GitHub/Phone OTP)"),
                AppFeature("admin", "Admin Panel", "🛡️", "Management", "Role-based access, user management, metrics"),
                AppFeature("payments", "Payment Gateway", "💳", "Finances", "Stripe / Razorpay / In-app purchases with webhooks"),
                AppFeature("notifications", "Push Notifications", "🔔", "Engagement", "FCM / APNs alerts, deep-linking, background triggers"),
                AppFeature("dark_mode", "Dark / Light Mode", "🌓", "UI/UX", "System-aware theme toggle with persistent preferences"),
                AppFeature("ai_ml", "AI / ML Features", "🤖", "Intelligence", "LLM integrations, vision, smart recommendations"),
                AppFeature("file_upload", "File & Media Upload", "📁", "Media", "Cloudinary / Firebase Storage / S3 image & video uploads"),
                AppFeature("offline_db", "Offline Support & Local DB", "💾", "Persistence", "Room / SQLite / Hive local caching with sync"),
                AppFeature("i18n", "Multi-Language (i18n)", "🌐", "Localization", "English, Hindi, Spanish, and dynamic locale switching"),
                AppFeature("analytics", "Analytics Dashboard", "📊", "Data", "Event tracking, interactive charts, conversion metrics"),
                AppFeature("live_updates", "Live Updates & WebSockets", "⚡", "Realtime", "Real-time socket connection, instant state sync"),
                AppFeature("chat", "Real-Time Chat Screen", "💬", "Communication", "1-on-1 and group messaging, typing indicators, read receipts"),
                AppFeature("tournament", "Tournament Brackets & Leaderboard", "🏆", "Gaming", "Single/Double elimination bracket, match fixtures, ELO rankings"),
                AppFeature("user_profile", "User Profile & Settings", "👤", "User", "Avatar upload, bio, stats, notification preferences"),
                AppFeature("search_filter", "Search & Advanced Filters", "🔍", "Navigation", "Full-text search, multi-tag sorting and debounce"),
                AppFeature("social_share", "Social Sharing & Deep Links", "🔗", "Virality", "Native OS share sheet, custom invite URLs"),
                AppFeature("rest_api", "REST API & Webhook Services", "📡", "Networking", "Clean API client with interceptors, error mapping, caching")
            )
        }
    }
}
