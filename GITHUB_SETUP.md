# 📤 Pushing to GitHub Guide

This guide will help you push your AI Fraud Guard project to GitHub with proper secret management.

## ✅ Pre-Push Checklist

Before pushing to GitHub, ensure:

- [ ] `apikeys.properties` is in `.gitignore`
- [ ] `google-services.json` is in `.gitignore` (if you want to keep it private)
- [ ] No API keys are hardcoded in source files
- [ ] Build is successful locally
- [ ] All sensitive data is removed

## 🚀 Step-by-Step Guide

### 1. Initialize Git Repository (if not already done)

```bash
cd /path/to/ai-fraud-guard
git init
```

### 2. Verify .gitignore

Check that `.gitignore` includes:

```gitignore
# API Keys and Secrets
apikeys.properties
secrets.properties
local.properties

# Optional: Keep google-services.json private
# google-services.json
```

### 3. Check What Will Be Committed

```bash
git status
```

**Important**: Verify that `apikeys.properties` is NOT listed!

### 4. Add Files to Git

```bash
git add .
```

### 5. Verify No Secrets Are Staged

```bash
# Check for API keys in staged files
git diff --cached | grep -i "api_key"
git diff --cached | grep -i "gemini"
```

If you see any API keys, **STOP** and remove them!

### 6. Create Initial Commit

```bash
git commit -m "Initial commit: AI Fraud Guard app with secure API key management"
```

### 7. Create GitHub Repository

1. Go to [GitHub](https://github.com/)
2. Click "New Repository"
3. Name: `ai-fraud-guard`
4. Description: "Android app for fraud detection with AI assistance"
5. Choose: **Public** or **Private**
6. **DO NOT** initialize with README (we already have one)
7. Click "Create Repository"

### 8. Add Remote and Push

```bash
# Add GitHub remote
git remote add origin https://github.com/YOUR_USERNAME/ai-fraud-guard.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## 🔐 Setting Up GitHub Secrets (for CI/CD)

If you want to set up GitHub Actions for automated builds:

### 1. Go to Repository Settings

Navigate to: `Settings` → `Secrets and variables` → `Actions`

### 2. Add Repository Secrets

Click "New repository secret" and add:

**Secret Name**: `NEWS_API_KEY`
**Value**: Your NewsAPI key

**Secret Name**: `GEMINI_API_KEY`
**Value**: Your Gemini API key

### 3. Create GitHub Actions Workflow (Optional)

Create `.github/workflows/android.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Create apikeys.properties
      run: |
        echo "NEWS_API_KEY=${{ secrets.NEWS_API_KEY }}" > apikeys.properties
        echo "GEMINI_API_KEY=${{ secrets.GEMINI_API_KEY }}" >> apikeys.properties
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew build -x lintDebug
```

## ⚠️ Important Security Notes

### If You Accidentally Committed Secrets

**DON'T PANIC!** Follow these steps:

1. **Immediately rotate your API keys**:
   - Get new NewsAPI key
   - Get new Gemini API key
   - Update `apikeys.properties`

2. **Remove from Git history**:
   ```bash
   # Install BFG Repo-Cleaner
   # Download from: https://rtyley.github.io/bfg-repo-cleaner/
   
   # Remove the file from history
   bfg --delete-files apikeys.properties
   
   # Clean up
   git reflog expire --expire=now --all
   git gc --prune=now --aggressive
   
   # Force push (WARNING: This rewrites history!)
   git push origin --force --all
   ```

3. **Alternative: Delete and recreate repository**:
   - Delete the GitHub repository
   - Create a new one
   - Push again with secrets removed

## 📝 Updating README

Before pushing, update `README.md` with:

1. Your GitHub username in URLs
2. Your contact information
3. Screenshots (if available)
4. Any project-specific instructions

## 🔄 Regular Updates

When making changes:

```bash
# Check status
git status

# Add changes
git add .

# Commit
git commit -m "Description of changes"

# Push
git push origin main
```

## 👥 Collaborating

### For Contributors

1. Fork the repository
2. Clone your fork
3. Create `apikeys.properties` from template
4. Add your own API keys
5. Make changes
6. Push to your fork
7. Create Pull Request

### For Repository Owner

1. Review Pull Requests
2. Ensure no secrets are included
3. Test changes locally
4. Merge if approved

## 📚 Additional Resources

- [GitHub Docs - Managing Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Git Ignore Documentation](https://git-scm.com/docs/gitignore)
- [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)

## ✅ Final Verification

After pushing, verify on GitHub:

- [ ] `apikeys.properties` is NOT visible
- [ ] `apikeys.properties.template` IS visible
- [ ] README.md displays correctly
- [ ] .gitignore is present
- [ ] No API keys in any files

---

**Congratulations!** 🎉 Your project is now on GitHub with proper secret management!
