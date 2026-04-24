# Gemini AI Setup Guide

## Get Your Free Gemini API Key

### Step 1: Visit Google AI Studio
1. Go to [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account

### Step 2: Create API Key
1. Click "Create API Key"
2. Select a Google Cloud project (or create a new one)
3. Copy the generated API key

### Step 3: Add to Your App
1. Open `app/src/main/java/com/example/aifraudguard/ApiConfig.kt`
2. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual API key:

```kotlin
const val GEMINI_API_KEY = "your_actual_api_key_here"
```

## Features

### AI Assistant Capabilities
- **Scam Detection**: Identify potential scams and fraud
- **Cybercrime Education**: Learn about different types of cyber threats
- **Safety Tips**: Get personalized advice on staying safe online
- **Incident Reporting**: Guidance on reporting suspicious activities

### How to Use
1. **Swipe Right** from the news feed to access AI Assistant
2. **Type your question** about scams, fraud, or cybersecurity
3. **Get instant answers** powered by Gemini AI
4. **Swipe Left** to return to news feed

### Example Questions
- "How can I identify a phishing email?"
- "What should I do if I receive a suspicious call?"
- "Is this message a scam?" (describe the message)
- "How to protect my bank account from fraud?"
- "What are common WhatsApp scams?"

## API Limits
- **Free Tier**: 60 requests per minute
- **Generous quota** for personal use
- No credit card required

## Privacy
- Your conversations are processed by Google's Gemini AI
- No conversation history is stored on our servers
- Messages are sent securely over HTTPS

## Troubleshooting

### "API key not configured" error
- Make sure you've added your API key to `ApiConfig.kt`
- Rebuild the app after adding the key

### "Error getting AI response"
- Check your internet connection
- Verify your API key is correct
- Check if you've exceeded the rate limit (60 requests/minute)

### No response from AI
- Wait a few seconds - AI responses can take 2-5 seconds
- Try a simpler question
- Check the app logs for error messages