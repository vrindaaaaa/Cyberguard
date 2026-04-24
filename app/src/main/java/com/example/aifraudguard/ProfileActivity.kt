package com.example.aifraudguard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.aifraudguard.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadUserProfile()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadUserProfile() {
        val currentUser = AuthHelper.getCurrentUser()
        val sharedPrefs = getSharedPreferences("FraudGuardPrefs", MODE_PRIVATE)

        // Load profile photo
        currentUser?.photoUrl?.let { photoUrl ->
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_person)
                .into(binding.ivProfilePhoto)
        }

        // Load user data
        binding.tvName.text = sharedPrefs.getString("USER_NAME", "Not provided") ?: "Not provided"
        binding.tvEmail.text = currentUser?.email ?: "Not provided"
        binding.tvPhone.text = sharedPrefs.getString("USER_PHONE_NUMBER", "Not provided") ?: "Not provided"
        binding.tvGender.text = sharedPrefs.getString("USER_GENDER", "Not provided") ?: "Not provided"
        binding.tvDob.text = sharedPrefs.getString("USER_DOB", "Not provided") ?: "Not provided"
        binding.tvAge.text = sharedPrefs.getString("USER_AGE", "Not provided") ?: "Not provided"
        binding.tvCountry.text = sharedPrefs.getString("USER_COUNTRY", "Not provided") ?: "Not provided"
        binding.tvState.text = sharedPrefs.getString("USER_STATE", "Not provided") ?: "Not provided"
        binding.tvDistrict.text = sharedPrefs.getString("USER_DISTRICT", "Not provided") ?: "Not provided"
        binding.tvCity.text = sharedPrefs.getString("USER_CITY", "Not provided") ?: "Not provided"
    }
}
