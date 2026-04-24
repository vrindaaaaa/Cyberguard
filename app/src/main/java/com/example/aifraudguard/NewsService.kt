package com.example.aifraudguard

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewsService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    // Get API key from config
    private val NEWS_API_KEY = ApiConfig.NEWS_API_KEY
    
    companion object {
        private const val TAG = "NewsService"
        private const val NEWS_API_BASE_URL = "https://newsapi.org/v2/everything"
    }
    
    suspend fun getScamNews(): List<NewsArticle> = withContext(Dispatchers.IO) {
        // Get articles from NewsAPI only
        val newsApiArticles = getScamNewsFromNewsAPI()
        
        // Remove duplicates based on title and return up to 15 articles
        val uniqueArticles = newsApiArticles
            .distinctBy { it.title.lowercase().trim() }
            .sortedByDescending { it.publishedAt }
            .take(15)
        
        return@withContext if (uniqueArticles.isNotEmpty()) {
            Log.d(TAG, "Successfully loaded ${uniqueArticles.size} articles from NewsAPI")
            uniqueArticles
        } else {
            Log.w(TAG, "No articles found, returning mock data")
            getMockScamNews()
        }
    }
    

    
    private suspend fun getScamNewsFromNewsAPI(): List<NewsArticle> = withContext(Dispatchers.IO) {
        if (NEWS_API_KEY == "YOUR_NEWS_API_KEY_HERE") {
            Log.w(TAG, "NewsAPI key not configured")
            return@withContext emptyList()
        }
        
        try {
            // Get date from 7 days ago for recent news
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val fromDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            
            // Search terms for scam-related news
            val query = "scam OR fraud OR phishing OR cybercrime OR digital fraud OR online scam"
            
            val url = "$NEWS_API_BASE_URL?q=$query&from=$fromDate&sortBy=publishedAt&language=en&apiKey=$NEWS_API_KEY"
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val newsResponse = gson.fromJson(responseBody, NewsResponse::class.java)
                    Log.d(TAG, "Successfully fetched ${newsResponse.articles.size} news articles from NewsAPI")
                    
                    return@withContext newsResponse.articles
                        .filter { article ->
                            val title = article.title.lowercase()
                            val description = article.description?.lowercase() ?: ""
                            
                            title.contains("scam") || title.contains("fraud") || 
                            title.contains("phishing") || title.contains("cyber") ||
                            description.contains("scam") || description.contains("fraud") ||
                            description.contains("phishing") || description.contains("cyber")
                        }
                        .take(15)
                } else {
                    Log.e(TAG, "Empty response body from NewsAPI")
                    return@withContext emptyList()
                }
            } else {
                Log.e(TAG, "NewsAPI request failed: ${response.code} ${response.message}")
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching news from NewsAPI", e)
            return@withContext emptyList()
        }
    }
    

    

    
    private fun getMockScamNews(): List<NewsArticle> {
        val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
        
        return listOf(
            NewsArticle(
                source = NewsSource(null, "Cyber Security News"),
                author = "Security Team",
                title = "New WhatsApp Scam Targets Users with Fake Prize Messages",
                description = "Cybercriminals are using fake lottery and prize messages to steal personal information from WhatsApp users across India.",
                url = "https://example.com/whatsapp-scam",
                urlToImage = "https://via.placeholder.com/300x200/FF5722/FFFFFF?text=WhatsApp+Scam",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "Financial Times"),
                author = "Fraud Reporter",
                title = "Banking Fraud Increases 40% with New OTP Scam Techniques",
                description = "Banks warn customers about sophisticated OTP scams where fraudsters impersonate bank officials to steal sensitive information.",
                url = "https://example.com/banking-fraud",
                urlToImage = "https://via.placeholder.com/300x200/2196F3/FFFFFF?text=Banking+Fraud",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "Tech Alert"),
                author = "Cyber Team",
                title = "Phishing Attacks Rise During Festival Season",
                description = "Security experts report a significant increase in phishing attempts targeting online shoppers during the festival season.",
                url = "https://example.com/phishing-attacks",
                urlToImage = "https://via.placeholder.com/300x200/9C27B0/FFFFFF?text=Phishing+Alert",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "India Today"),
                author = "Crime Reporter",
                title = "UPI Fraud Cases Surge 300% in Major Indian Cities",
                description = "Digital payment frauds through UPI apps have seen a massive increase, with scammers using sophisticated social engineering techniques.",
                url = "https://example.com/upi-fraud",
                urlToImage = "https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=UPI+Fraud",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "Economic Times"),
                author = "Finance Desk",
                title = "Cryptocurrency Scams Target Young Investors with Fake Apps",
                description = "Fraudulent cryptocurrency trading apps are being used to steal money from inexperienced investors, especially targeting college students.",
                url = "https://example.com/crypto-scam",
                urlToImage = "https://via.placeholder.com/300x200/FF9800/FFFFFF?text=Crypto+Scam",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "Times of India"),
                author = "Tech Reporter",
                title = "Fake Job Offer Scams Increase During Economic Uncertainty",
                description = "Scammers are exploiting job seekers with fake employment offers, demanding upfront fees for non-existent positions.",
                url = "https://example.com/job-scam",
                urlToImage = "https://via.placeholder.com/300x200/795548/FFFFFF?text=Job+Scam",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "NDTV"),
                author = "Cyber Crime Unit",
                title = "Romance Scams on Dating Apps Cost Indians ₹50 Crores",
                description = "Online dating platforms are being misused by international scammers to defraud Indian users through fake romantic relationships.",
                url = "https://example.com/romance-scam",
                urlToImage = "https://via.placeholder.com/300x200/E91E63/FFFFFF?text=Romance+Scam",
                publishedAt = currentDate,
                content = null
            ),
            NewsArticle(
                source = NewsSource(null, "Business Standard"),
                author = "Security Analyst",
                title = "SIM Swap Fraud: New Technique Bypasses Two-Factor Authentication",
                description = "Cybercriminals are using SIM swapping to bypass 2FA security measures and gain access to bank accounts and digital wallets.",
                url = "https://example.com/sim-swap",
                urlToImage = "https://via.placeholder.com/300x200/607D8B/FFFFFF?text=SIM+Swap",
                publishedAt = currentDate,
                content = null
            )
        )
    }
}