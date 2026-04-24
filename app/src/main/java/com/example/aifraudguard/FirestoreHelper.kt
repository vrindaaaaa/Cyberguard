package com.example.aifraudguard

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreHelper {
    
    private const val TAG = "FirestoreHelper"
    private const val USERS_COLLECTION = "users"
    
    private val db: FirebaseFirestore by lazy { Firebase.firestore }
    
    /**
     * Save user profile to Firestore
     */
    fun saveUserProfile(
        userProfile: UserProfile,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(USERS_COLLECTION)
            .document(userProfile.userId)
            .set(userProfile)
            .addOnSuccessListener {
                Log.d(TAG, "User profile saved successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error saving user profile", exception)
                onFailure(exception)
            }
    }
    
    /**
     * Get user profile from Firestore
     */
    fun getUserProfile(
        userId: String,
        onSuccess: (UserProfile?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    Log.d(TAG, "User profile retrieved successfully")
                    onSuccess(profile)
                } else {
                    Log.d(TAG, "User profile does not exist")
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting user profile", exception)
                onFailure(exception)
            }
    }
    
    /**
     * Update user profile in Firestore
     */
    fun updateUserProfile(
        userId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updatesWithTimestamp = updates.toMutableMap()
        updatesWithTimestamp["updatedAt"] = System.currentTimeMillis()
        
        db.collection(USERS_COLLECTION)
            .document(userId)
            .update(updatesWithTimestamp)
            .addOnSuccessListener {
                Log.d(TAG, "User profile updated successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating user profile", exception)
                onFailure(exception)
            }
    }
    
    /**
     * Check if user profile exists in Firestore
     */
    fun checkUserProfileExists(
        userId: String,
        onResult: (Boolean) -> Unit
    ) {
        db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
    
    /**
     * Save user profile to local SharedPreferences
     */
    fun saveToLocalStorage(context: Context, profile: UserProfile) {
        val sharedPrefs = context.getSharedPreferences("FraudGuardPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("USER_NAME", profile.name)
            putString("USER_EMAIL", profile.email)
            putString("USER_PHONE_NUMBER", profile.phoneNumber)
            putString("USER_GENDER", profile.gender)
            putString("USER_DOB", profile.dateOfBirth)
            putString("USER_AGE", profile.age)
            putString("USER_COUNTRY", profile.country)
            putString("USER_STATE", profile.state)
            putString("USER_DISTRICT", profile.district)
            putString("USER_CITY", profile.city)
            putString("USER_ID", profile.userId)
            putBoolean("USER_INFO_COMPLETED", true)
            apply()
        }
    }
}
