package com.example.aifraudguard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsProgressBar: ProgressBar
    private lateinit var newsErrorText: TextView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsService: NewsService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsRecyclerView = view.findViewById(R.id.newsRecyclerView)
        newsProgressBar = view.findViewById(R.id.newsProgressBar)
        newsErrorText = view.findViewById(R.id.newsErrorText)

        setupNewsSection()
        loadScamNews()
    }

    private fun setupNewsSection() {
        newsService = NewsService()
        newsAdapter = NewsAdapter(emptyList())

        newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun loadScamNews() {
        newsProgressBar.visibility = View.VISIBLE
        newsErrorText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val articles = newsService.getScamNews()

                if (articles.isNotEmpty()) {
                    newsAdapter.updateArticles(articles)
                    newsRecyclerView.visibility = View.VISIBLE
                    newsErrorText.visibility = View.GONE
                    Log.d("NewsFragment", "Loaded ${articles.size} news articles")
                } else {
                    newsRecyclerView.visibility = View.GONE
                    newsErrorText.visibility = View.VISIBLE
                    newsErrorText.text = "No recent scam news available. Stay vigilant!"
                }
            } catch (e: Exception) {
                Log.e("NewsFragment", "Error loading news", e)
                newsRecyclerView.visibility = View.GONE
                newsErrorText.visibility = View.VISIBLE
                newsErrorText.text = "Unable to load news. Please check your internet connection."
            } finally {
                newsProgressBar.visibility = View.GONE
            }
        }
    }
}