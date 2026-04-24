package com.example.aifraudguard

object ApiConfig {
    // API keys are loaded from apikeys.properties file (not committed to git)
    // See apikeys.properties.template for setup instructions
    
    val NEWS_API_KEY: String
        get() = BuildConfig.NEWS_API_KEY.ifEmpty { "YOUR_NEWS_API_KEY_HERE" }
    
    val GEMINI_API_KEY: String
        get() = BuildConfig.GEMINI_API_KEY.ifEmpty { "YOUR_GEMINI_API_KEY_HERE" }
}