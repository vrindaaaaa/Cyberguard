# News API Setup Instructions

## Getting Started with SerpAPI (Recommended)

To display real-time scam news in your FraudGuard app, you can use either SerpAPI or NewsAPI. SerpAPI is recommended as it provides Google News results with better coverage.

### Option 1: SerpAPI (Recommended)

#### Step 1: Get a Free SerpAPI Key

1. Visit [SerpAPI.com](https://serpapi.com/)
2. Click "Sign Up" and create a free account
3. Go to your dashboard and copy your API key
4. Free tier includes 100 searches per month

#### Step 2: Configure the SerpAPI Key

1. Open `app/src/main/java/com/example/aifraudguard/ApiConfig.kt`
2. Replace `YOUR_SERP_API_KEY_HERE` with your actual API key:

```kotlin
const val SERP_API_KEY = "your_actual_serpapi_key_here"
```

### Option 2: NewsAPI (Alternative)

#### Step 1: Get a Free NewsAPI Key

1. Visit [NewsAPI.org](https://newsapi.org/)
2. Click "Get API Key" 
3. Sign up for a free account
4. Copy your API key from the dashboard

#### Step 2: Configure the NewsAPI Key

1. Open `app/src/main/java/com/example/aifraudguard/ApiConfig.kt`
2. Replace `YOUR_NEWS_API_KEY_HERE` with your actual API key:

```kotlin
const val NEWS_API_KEY = "your_actual_newsapi_key_here"
```

### How It Works

The app uses a smart fallback system:
1. **First**: Tries SerpAPI for Google News results
2. **Second**: Falls back to NewsAPI if SerpAPI fails
3. **Third**: Shows mock data if both APIs fail

### News Sources

The app is configured to fetch scam-related news from:
- **SerpAPI**: Google News results (global and India-specific)
- **NewsAPI**: Various news sources including Indian publications
- **Smart Filtering**: Only shows scam/fraud/cybercrime related articles

### Features

- **Real-time Updates**: Fetches latest news from Google News
- **Smart Filtering**: Only shows scam/fraud/cybercrime related articles
- **Fallback System**: Multiple API sources ensure reliability
- **Click to Read**: Tap any news item to read the full article
- **Responsive Design**: Optimized for mobile viewing
- **Indian Focus**: Special support for India-specific scam news

### API Limits

**SerpAPI (Recommended):**
- Free tier: 100 searches per month
- More reliable and comprehensive results
- Better coverage of recent news

**NewsAPI (Fallback):**
- Free tier: 1,000 requests per month
- Rate limit: 1 request per second

### Troubleshooting

If news doesn't load:
1. Check your internet connection
2. Verify your API key is correct in `ApiConfig.kt`
3. Check the app logs for error messages
4. The app will automatically show mock data if APIs fail
5. SerpAPI has better reliability than NewsAPI

### Privacy Note

The app only fetches news headlines and descriptions. No personal data is sent to the APIs. Both SerpAPI and NewsAPI are GDPR compliant.

### Getting Your API Keys

**For SerpAPI:**
1. Go to [serpapi.com](https://serpapi.com/)
2. Sign up for free
3. Copy your API key from the dashboard
4. Paste it in `ApiConfig.kt`

**For NewsAPI (optional):**
1. Go to [newsapi.org](https://newsapi.org/)
2. Sign up for free
3. Copy your API key
4. Paste it in `ApiConfig.kt`

You only need one API key to get started, but having both provides better reliability.