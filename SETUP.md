# 🔧 Setup Guide for AI Fraud Guard

This guide will help you set up the project on your local machine.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (Arctic Fox or later)
- **JDK 8 or higher**
- **Git**
- **Android SDK** (API level 24 or higher)

## 🚀 Step-by-Step Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/ai-fraud-guard.git
cd ai-fraud-guard
```

### 2. Set Up API Keys

#### Create API Keys File

```bash
cp apikeys.properties.template apikeys.properties
```

#### Get NewsAPI Key

1. Go to [https://newsapi.org/](https://newsapi.org/)
2. Click "Get API Key"
3. Sign up for a free account
4. Copy your API key
5. Add it to `apikeys.properties`:
   ```properties
   NEWS_API_KEY=your_newsapi_key_here
   ```

#### Get Gemini AI Key

1. Go to [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy your API key
5. Add it to `apikeys.properties`:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

### 3. Set Up Firebase

#### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "AI Fraud Guard"
4. Follow the setup wizard

#### Add Android App to Firebase

1. In Firebase Console, click "Add app" → Android
2. Enter package name: `com.example.aifraudguard`
3. Download `google-services.json`
4. Place it in the `app/` directory

#### Enable Google Sign-In

1. In Firebase Console, go to "Authentication"
2. Click "Get Started"
3. Enable "Google" sign-in method
4. Add your SHA-1 fingerprint:
   ```bash
   # Get debug SHA-1
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
5. Copy the SHA-1 and add it in Firebase project settings

### 4. Open in Android Studio

1. Open Android Studio
2. Click "Open an Existing Project"
3. Navigate to the cloned repository
4. Wait for Gradle sync to complete

### 5. Build and Run

1. Connect your Android device or start an emulator
2. Click the "Run" button (green play icon)
3. Select your device
4. Wait for the app to install and launch

## 🔍 Troubleshooting

### Build Errors

**Problem**: `BuildConfig` not found
**Solution**: 
```bash
./gradlew clean
./gradlew build
```

**Problem**: API keys not loading
**Solution**: 
- Ensure `apikeys.properties` exists in the root directory
- Check that the file has correct format (no spaces around `=`)
- Rebuild the project

### Firebase Issues

**Problem**: Google Sign-In not working
**Solution**:
- Verify `google-services.json` is in `app/` directory
- Check SHA-1 fingerprint is added in Firebase Console
- Ensure Google Sign-In is enabled in Firebase Authentication

### API Issues

**Problem**: News not loading
**Solution**:
- Verify NewsAPI key is valid
- Check internet connection
- Look at Logcat for error messages

**Problem**: AI Assistant not responding
**Solution**:
- Verify Gemini API key is valid
- Check API quota limits
- Look at Logcat for error messages

## 📱 Testing

### Test on Emulator

1. Create an emulator in Android Studio (API 24+)
2. Start the emulator
3. Run the app

### Test on Physical Device

1. Enable Developer Options on your device
2. Enable USB Debugging
3. Connect device via USB
4. Run the app

## 🔐 Security Notes

- **Never commit** `apikeys.properties` to version control
- **Never share** your API keys publicly
- **Use environment variables** for CI/CD pipelines
- **Rotate keys** if accidentally exposed

## 📚 Additional Resources

- [Android Developer Guide](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Firebase Documentation](https://firebase.google.com/docs)
- [NewsAPI Documentation](https://newsapi.org/docs)
- [Gemini API Documentation](https://ai.google.dev/docs)

## 💡 Tips

- Use Android Studio's Logcat to debug issues
- Check `build.gradle.kts` for dependency versions
- Keep your API keys secure
- Test on multiple devices/screen sizes

## 🆘 Need Help?

If you encounter any issues:

1. Check the [Troubleshooting](#troubleshooting) section
2. Search existing GitHub issues
3. Create a new issue with:
   - Error message
   - Steps to reproduce
   - Android version
   - Device model

## ✅ Verification Checklist

Before running the app, ensure:

- [ ] `apikeys.properties` file exists with valid keys
- [ ] `google-services.json` is in `app/` directory
- [ ] Gradle sync completed successfully
- [ ] No build errors in Android Studio
- [ ] Device/emulator is running Android 7.0 (API 24) or higher
- [ ] Internet connection is available

---

Happy coding! 🚀
