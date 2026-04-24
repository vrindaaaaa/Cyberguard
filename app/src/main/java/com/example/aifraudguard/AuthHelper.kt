package com.example.aifraudguard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Helper class for Firebase Authentication operations
 */
object AuthHelper {
    
    private const val TAG = "AuthHelper"
    private const val PREFS_NAME = "FraudGuardPrefs"
    private const val KEY_USER_NAME = "USER_NAME"
    private const val KEY_USER_EMAIL = "USER_EMAIL"
    private const val KEY_USER_PHONE = "USER_PHONE_NUMBER"
    private const val KEY_USER_ID = "USER_ID"
    
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Check if user is authenticated with Firebase
     */
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Get current Firebase user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    /**
     * Get Google Sign-In client
     */
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        return GoogleSignIn.getClient(context, gso)
    }
    
    /**
     * Handle Google Sign-In result
     */
    fun handleGoogleSignInResult(
        task: Task<GoogleSignInAccount>,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!, onSuccess, onFailure)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed", e)
            onFailure(e)
        }
    }
    
    /**
     * Authenticate with Firebase using Google credentials
     */
    private fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    Log.d(TAG, "Firebase auth successful: ${user.email}")
                    onSuccess(user)
                } else {
                    onFailure(Exception("User is null after authentication"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Firebase auth failed", exception)
                onFailure(exception)
            }
    }
    
    /**
     * Sign in with email and password
     */
    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    Log.d(TAG, "Email sign in successful: ${user.email}")
                    onSuccess(user)
                } else {
                    onFailure(Exception("User is null after authentication"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Email sign in failed", exception)
                onFailure(exception)
            }
    }
    
    /**
     * Create account with email and password
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    Log.d(TAG, "Email sign up successful: ${user.email}")
                    onSuccess(user)
                } else {
                    onFailure(Exception("User is null after registration"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Email sign up failed", exception)
                onFailure(exception)
            }
    }
    
    /**
     * Save user data to SharedPreferences
     */
    fun saveUserData(
        context: Context,
        name: String,
        email: String,
        phone: String? = null,
        userId: String? = null
    ) {
        getPrefs(context).edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PHONE, phone ?: "")
            putString(KEY_USER_ID, userId ?: "")
            apply()
        }
    }
    
    /**
     * Save Firebase user data to SharedPreferences
     */
    fun saveFirebaseUserData(context: Context, user: FirebaseUser) {
        saveUserData(
            context = context,
            name = user.displayName ?: user.email?.substringBefore("@") ?: "User",
            email = user.email ?: "",
            phone = user.phoneNumber,
            userId = user.uid
        )
    }
    
    /**
     * Get user name
     */
    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "") ?: ""
    }
    
    /**
     * Get user email
     */
    fun getUserEmail(context: Context): String {
        return getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }
    
    /**
     * Get user phone
     */
    fun getUserPhone(context: Context): String {
        return getPrefs(context).getString(KEY_USER_PHONE, "") ?: ""
    }
    
    /**
     * Sign out from Firebase and clear local data
     */
    fun signOut(context: Context) {
        // Sign out from Firebase
        auth.signOut()
        
        // Sign out from Google
        getGoogleSignInClient(context).signOut()
        
        // Clear SharedPreferences
        getPrefs(context).edit().clear().apply()
        
        Log.d(TAG, "User signed out successfully")
    }
}
