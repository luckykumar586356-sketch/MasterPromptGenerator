package com.sunleycoder.masterpromptgenerator.generator

import com.sunleycoder.masterpromptgenerator.data.model.AppFeature
import com.sunleycoder.masterpromptgenerator.data.model.TechStack

object OfflineMasterEngine {

    fun generateMasterPrompt(
        appIdea: String,
        techStackId: String,
        selectedFeatureIds: List<String>,
        targetAi: String = "Claude 3.5 Sonnet / ChatGPT 4o / Google Gemini",
        architecture: String = "Clean Architecture + MVVM Pattern",
        extraRequirements: String = ""
    ): String {
        val stack = TechStack.findById(techStackId)
        val allFeatures = AppFeature.getDefaultList()
        val activeFeatures = allFeatures.filter { selectedFeatureIds.contains(it.id) }

        val sb = StringBuilder()

        sb.append("You are an elite Staff Software Architect and Principal Full-Stack Engineer with 15+ years of experience building scalable, production-grade applications. You are mentoring me to build a complete, end-to-end production application.\n\n")
        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("🎯 MISSION & SYSTEM SPECIFICATION\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")

        sb.append("Build the following project:\n")
        sb.append("• **Application Concept**: $appIdea\n")
        sb.append("• **Target Technology Stack**: ${stack.name} (${stack.category})\n")
        sb.append("• **Architecture Pattern**: $architecture\n")
        sb.append("• **Optimized For**: $targetAi\n")
        sb.append("• **Production Readiness**: Level 5 (Fully implemented code, zero dummy comments, zero truncated files, strict error boundaries, type safety, and reactive state management).\n\n")

        if (extraRequirements.isNotBlank()) {
            sb.append("• **Specific User Requirements**: $extraRequirements\n\n")
        }

        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("📂 COMPREHENSIVE REPOSITORY & DIRECTORY ARCHITECTURE\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")

        sb.append(getDirectoryStructure(techStackId))
        sb.append("\n\n")

        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("📦 REQUIRED DEPENDENCIES & PACKAGE MANIFEST\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")

        sb.append(getDependenciesForStack(techStackId, selectedFeatureIds))
        sb.append("\n\n")

        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("🧩 MANDATORY FUNCTIONAL MODULES & FEATURES\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")

        if (activeFeatures.isEmpty()) {
            sb.append("• Core Application Workflow: End-to-end navigation, modern responsive Material/Glass UI, reactive state binding, and localized input validation.\n\n")
        } else {
            activeFeatures.forEachIndexed { index, feat ->
                sb.append("${index + 1}. **${feat.emoji} ${feat.name}**\n")
                sb.append("   - Scope: ${feat.description}\n")
                sb.append("   - Implementation Requirement: ${getFeatureDetails(feat.id, techStackId)}\n\n")
            }
        }

        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("🎨 UI/UX DESIGN SYSTEM & STYLING GUIDELINES\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")
        sb.append("1. **Theme Support**: Implements high-contrast Dark Mode and clean Light Mode with deep slate backgrounds, vibrant violet/cyan primary accents, and subtle glowing borders.\n")
        sb.append("2. **Micro-interactions**: Ripple touches, fluid loading skeletons, optimistic UI updates, and non-blocking snackbar notifications.\n")
        sb.append("3. **Ergonomics**: Bottom navigation bar or persistent drawer, thumb-reachable touch targets (>= 48dp), edge-to-edge system bar padding.\n\n")

        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("🛡️ SECURITY, ERROR BOUNDARIES & EDGE CASE HANDLING\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")
        sb.append("• **Offline & Resilience**: Graceful degradation when network disconnects; automatic retry with exponential backoff on transient errors.\n")
        sb.append("• **Input Sanitization**: Strict input validation against SQLi/XSS, empty states, and character limits.\n")
        sb.append("• **State Safety**: Sealed classes / immutable state containers representing [Initial, Loading, Success(data), Error(message)].\n\n")

        sb.append("═══════════════════════════════════════════════════════════\n")
        sb.append("⚡ AI EXECUTION INSTRUCTIONS (CRITICAL DIRECTIVE)\n")
        sb.append("═══════════════════════════════════════════════════════════\n\n")
        sb.append("1. Do NOT reply with conversational pleasantries like 'Sure! Here is your code'.\n")
        sb.append("2. Begin IMMEDIATELY with the setup instructions followed by complete, copy-pasteable files.\n")
        sb.append("3. Write FULL source code for each file. NEVER truncate with `// TODO: implement here` or `// ... rest of code`.\n")
        sb.append("4. Provide each file with its exact relative path header so I can copy it directly into my IDE or build environment.\n")
        sb.append("5. Ensure all imports, models, and dependencies match exactly with zero unresolved references.")

        return sb.toString()
    }

    private fun getDirectoryStructure(stackId: String): String {
        return when (stackId) {
            "flutter" -> """
my_app/
├── android/
├── ios/
├── pubspec.yaml
└── lib/
    ├── main.dart
    ├── core/
    │   ├── constants/ (app_colors.dart, app_strings.dart)
    │   ├── theme/ (app_theme.dart)
    │   └── network/ (api_client.dart, endpoints.dart)
    ├── data/
    │   ├── models/
    │   ├── datasources/ (local_storage.dart, remote_api.dart)
    │   └── repositories/
    ├── domain/
    │   └── models/
    └── presentation/
        ├── controllers/ (state management providers/bloc/cubit)
        ├── screens/ (home/, details/, auth/, settings/)
        └── widgets/ (common components, buttons, custom cards)
""".trim()

            "kotlin_compose" -> """
app/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/myapp/
    │   ├── MainActivity.kt
    │   ├── ui/
    │   │   ├── theme/ (Color.kt, Theme.kt, Type.kt)
    │   │   ├── navigation/ (NavGraph.kt, Screen.kt)
    │   │   ├── screens/ (home/, details/, auth/)
    │   │   └── components/ (Cards.kt, Buttons.kt, Dialogs.kt)
    │   ├── data/
    │   │   ├── model/
    │   │   ├── local/ (AppDatabase.kt, EntityDao.kt)
    │   │   └── remote/ (ApiService.kt, RetrofitClient.kt)
    │   └── viewmodel/ (MainViewModel.kt, AuthViewModel.kt)
    └── res/ (drawable/, values/, mipmap/)
""".trim()

            "react_native" -> """
src/
├── api/ (client.ts, endpoints.ts)
├── assets/ (icons/, images/)
├── components/ (common/Button.tsx, Card.tsx, Header.tsx)
├── navigation/ (AppNavigator.tsx, RootParamList.ts)
├── screens/ (HomeScreen.tsx, AuthScreen.tsx, DetailScreen.tsx)
├── state/ (store.ts, slices/ or context/)
├── types/ (models.d.ts)
├── utils/ (storage.ts, helpers.ts)
├── App.tsx
├── package.json
└── tsconfig.json
""".trim()

            "html_single" -> """
Single Self-Contained Document:
index.html
├── <!DOCTYPE html>
├── <head>
│   ├── Responsive Viewport & Meta Tags
│   ├── TailwindCSS / Modern Font Stylesheet CDN
│   └── <style> Custom Glassmorphism, Animations, Dark Palette </style>
├── <body>
│   ├── Header Bar & Navigation Tabs
│   ├── Main Dashboard / Interactive Workspace Container
│   ├── Modals, Floating Panels & Toast System
│   └── <script>
│       ├── State Management (Reactive LocalStorage Store)
│       ├── API / WebSocket / Mock Engine
│       ├── Event Listeners & UI Renders
│       └── Toast & Clipboard Controllers
│   </script>
""".trim()

            else -> """
src/
├── config/ (settings, constants)
├── controllers/ (request handlers)
├── services/ (business logic)
├── models/ (schemas & data structures)
├── routes/ (endpoints)
├── middlewares/ (auth, logging, error handling)
└── index.ts / main.py / App.kt
""".trim()
        }
    }

    private fun getDependenciesForStack(stackId: String, featureIds: List<String>): String {
        return when (stackId) {
            "flutter" -> """
dependencies:
  flutter:
    sdk: flutter
  google_fonts: ^6.1.0
  provider: ^6.1.1 # or flutter_bloc: ^8.1.3
  http: ^1.2.0
  shared_preferences: ^2.2.2
  cached_network_image: ^3.3.1
  flutter_staggered_animations: ^1.1.1
  uuid: ^4.3.3
""".trim()

            "kotlin_compose" -> """
dependencies:
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
""".trim()

            "react_native" -> """
dependencies:
    "@react-navigation/native": "^6.1.9",
    "@react-navigation/native-stack": "^6.9.17",
    "react-native-safe-area-context": "^4.8.2",
    "react-native-screens": "^3.29.0",
    "lucide-react-native": "^0.300.0",
    "axios": "^1.6.5",
    "@react-native-async-storage/async-storage": "^1.21.0"
""".trim()

            "html_single" -> """
CDN Dependencies (Zero Local Build Tooling Needed):
<!-- Tailwind CSS Play CDN -->
<script src="https://cdn.tailwindcss.com"></script>
<!-- Lucide Icons -->
<script src="https://unpkg.com/lucide@latest"></script>
<!-- Canvas Confetti & Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/canvas-confetti@1.6.0/dist/confetti.browser.min.js"></script>
""".trim()

            else -> """
# Standard Modern Dependencies
- Network HTTP / WebSocket Client
- JSON Serializer & Deserializer
- State / Cache Storage Engine
- UI Theme & Animation Suite
""".trim()
        }
    }

    private fun getFeatureDetails(featureId: String, stackId: String): String {
        return when (featureId) {
            "auth" -> "Build complete user signup, email/password login, JWT/Token handling, persistent session storage, and form validation."
            "admin" -> "Comprehensive admin dashboard with analytics stats, ban/unban user actions, data moderation tables, and access control."
            "payments" -> "Secure checkout flow, mock/live gateway integration (Stripe/Razorpay tokens), transaction history receipt, and webhook handler."
            "notifications" -> "Interactive notification center, local schedule alerts, and FCM push notification receiver listener."
            "dark_mode" -> "Dynamic theme toggle between OLED Dark and Clean Light with persistent preference saving."
            "ai_ml" -> "Prompt ingestion pipeline, streaming LLM chat interface, token counter, and formatted code highlighter."
            "file_upload" -> "Image picker with mime-type checking, file size compression, progress upload bar, and cloud storage URL binding."
            "offline_db" -> "Complete local caching architecture, offline CRUD persistence, and background cloud synchronization."
            "i18n" -> "Key-value localization dictionary supporting instant runtime language change without app restart."
            "analytics" -> "Interactive data visualization, bar/line charts, and user conversion tracking metrics."
            "live_updates" -> "Persistent WebSocket client connection, real-time message broadcasting, reconnection retry logic, and heartbeat ping/pong."
            "chat" -> "Chat conversation view with bubble styling, timestamps, typing indicators, auto-scroll to bottom, and image attachments."
            "tournament" -> "Interactive single & double elimination bracket tree view, match scheduling, score input with winner propagation, and live leaderboard rankings."
            "user_profile" -> "Editable profile screen with avatar selector, bio, account stats counter, password change, and logout action."
            "search_filter" -> "Debounced live search bar, category chips filter, sorting by date/name/popularity, and highlighted search results."
            "social_share" -> "Native intent share dialog, custom deep-link invite generator, and copyable invite code."
            "rest_api" -> "Production HTTP client with authorization headers, response interceptors, custom error codes, and request retry policy."
            else -> "Fully implemented reactive state with robust error handling and clean UI feedback."
        }
    }
}
