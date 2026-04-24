package com.example.aifraudguard

import com.google.gson.annotations.SerializedName

// SerpAPI Response Models
data class SerpNewsResponse(
    @SerializedName("news_results") val newsResults: List<SerpNewsArticle>?,
    @SerializedName("search_metadata") val searchMetadata: SerpSearchMetadata?
)

data class SerpNewsArticle(
    @SerializedName("position") val position: Int,
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("snippet") val snippet: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("source") val source: String,
    @SerializedName("thumbnail") val thumbnail: String?
)

data class SerpSearchMetadata(
    @SerializedName("status") val status: String?,
    @SerializedName("total_results") val totalResults: String?
)

// Unified NewsArticle model for the app
data class NewsArticle(
    val source: NewsSource,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

data class NewsSource(
    val id: String?,
    val name: String
)

// Legacy NewsAPI models (keeping for compatibility)
data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<NewsArticle>
)