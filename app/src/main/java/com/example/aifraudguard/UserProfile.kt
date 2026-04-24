package com.example.aifraudguard

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val age: String = "",
    val country: String = "",
    val state: String = "",
    val district: String = "",
    val city: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
