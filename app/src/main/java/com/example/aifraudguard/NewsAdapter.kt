package com.example.aifraudguard

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private var articles: List<NewsArticle>) : 
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.newsTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.newsDescription)
        val sourceText: TextView = itemView.findViewById(R.id.newsSource)
        val dateText: TextView = itemView.findViewById(R.id.newsDate)
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        
        holder.titleText.text = article.title
        holder.descriptionText.text = article.description ?: "No description available"
        holder.sourceText.text = article.source.name
        
        // Format the date
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(article.publishedAt)
            holder.dateText.text = date?.let { outputFormat.format(it) } ?: article.publishedAt
        } catch (e: Exception) {
            holder.dateText.text = article.publishedAt
        }
        
        // Load image - all articles should now have image URLs (real or placeholder)
        android.util.Log.d("NewsAdapter", "Article: ${article.title}")
        android.util.Log.d("NewsAdapter", "Image URL: ${article.urlToImage}")
        
        if (!article.urlToImage.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_shield)
                .error(R.drawable.ic_shield)
                .centerCrop()
                .into(holder.newsImage)
            // Clear any color filter for actual images
            holder.newsImage.clearColorFilter()
            android.util.Log.d("NewsAdapter", "Loading image with Glide: ${article.urlToImage}")
        } else {
            android.util.Log.d("NewsAdapter", "No image URL, showing default icon")
            // Show default shield icon with tint
            holder.newsImage.setImageResource(R.drawable.ic_shield)
            holder.newsImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        }
        holder.newsImage.visibility = View.VISIBLE
        
        // Set click listener to open article in browser
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            holder.itemView.context.startActivity(intent)
        }
    }
    
    override fun getItemCount(): Int = articles.size
    
    fun updateArticles(newArticles: List<NewsArticle>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}