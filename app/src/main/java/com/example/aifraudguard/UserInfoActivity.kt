package com.example.aifraudguard

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.aifraudguard.databinding.ActivityUserInfoBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private var selectedDate: Calendar? = null
    private var selectedCountry: String = ""
    private var selectedState: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is authenticated
        if (!AuthHelper.isUserAuthenticated()) {
            // User not authenticated, redirect to AuthActivity
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupLocationDropdowns()
        checkExistingUserData()
        animateViews()
    }
    
    private fun checkExistingUserData() {
        val currentUser = AuthHelper.getCurrentUser()
        if (currentUser != null) {
            showLoading()
            
            // Check if user data exists in Firestore
            FirestoreHelper.getUserProfile(
                userId = currentUser.uid,
                onSuccess = { profile ->
                    hideLoading()
                    if (profile != null) {
                        // User data exists, save to local and navigate to main
                        FirestoreHelper.saveToLocalStorage(this, profile)
                        android.widget.Toast.makeText(
                            this,
                            "Welcome back, ${profile.name}!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        navigateToMainActivity()
                    }
                    // If profile is null, user needs to fill the form
                },
                onFailure = { exception ->
                    hideLoading()
                    // If error, let user fill the form
                    android.util.Log.e("UserInfoActivity", "Error checking user data", exception)
                }
            )
        }
    }

    private fun setupViews() {
        // Date of Birth picker
        binding.etDob.setOnClickListener {
            showDatePicker()
        }

        // Continue button
        binding.btnContinue.setOnClickListener {
            if (validateInputs()) {
                saveUserInfo()
            }
        }

        // Skip button
        binding.tvSkip.setOnClickListener {
            navigateToMainActivity()
        }
        
        // Debug: Long press on header to clear all data and restart
        binding.tvHeader.setOnLongClickListener {
            clearAllDataAndRestart()
            true
        }
    }

    private fun setupLocationDropdowns() {
        // Setup Country dropdown with filtering
        val countryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            LocationData.countries
        )
        binding.etCountry.setAdapter(countryAdapter)
        binding.etCountry.threshold = 1 // Show suggestions after 1 character
        
        // Show dropdown when user clicks on the field
        binding.etCountry.setOnClickListener {
            binding.etCountry.showDropDown()
        }
        
        binding.etCountry.setOnItemClickListener { _, _, position, _ ->
            selectedCountry = countryAdapter.getItem(position) ?: ""
            binding.etState.setText("")
            binding.etDistrict.setText("")
            binding.etCity.setText("")
            setupStateDropdown(selectedCountry)
        }

        // Initially disable state, district, city until country is selected
        binding.etState.isEnabled = false
        binding.etDistrict.isEnabled = false
        binding.etCity.isEnabled = false
    }

    private fun setupStateDropdown(country: String) {
        val states = LocationData.getStatesForCountry(country)
        
        if (states.isNotEmpty()) {
            val stateAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                states
            )
            binding.etState.setAdapter(stateAdapter)
            binding.etState.isEnabled = true
            binding.etState.threshold = 1
            
            // Show dropdown when clicked
            binding.etState.setOnClickListener {
                binding.etState.showDropDown()
            }
            
            binding.etState.setOnItemClickListener { _, _, position, _ ->
                selectedState = stateAdapter.getItem(position) ?: ""
                binding.etDistrict.setText("")
                binding.etCity.setText("")
                setupDistrictDropdown(country, selectedState)
            }
        } else {
            // For countries without predefined states, allow free text
            binding.etState.isEnabled = true
            binding.etState.setAdapter(null)
            binding.etDistrict.isEnabled = true
            binding.etDistrict.setAdapter(null)
            binding.etCity.isEnabled = true
            // City is now a regular TextInputEditText, no adapter needed
        }
    }

    private fun setupDistrictDropdown(country: String, state: String) {
        val districts = LocationData.getDistrictsForState(country, state)
        
        if (districts.isNotEmpty()) {
            val districtAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                districts
            )
            binding.etDistrict.setAdapter(districtAdapter)
            binding.etDistrict.isEnabled = true
            binding.etDistrict.threshold = 1
            
            // Show dropdown when clicked
            binding.etDistrict.setOnClickListener {
                binding.etDistrict.showDropDown()
            }
            
            binding.etDistrict.setOnItemClickListener { _, _, _, _ ->
                // Enable city field for manual input (no dropdown)
                binding.etCity.isEnabled = true
            }
        } else {
            // Allow free text for district and city
            binding.etDistrict.isEnabled = true
            binding.etDistrict.setAdapter(null)
            binding.etCity.isEnabled = true
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                
                val dateString = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDob.setText(dateString)
                
                // Calculate and display age
                calculateAndDisplayAge(selectedYear, selectedMonth, selectedDay)
            },
            year,
            month,
            day
        )

        // Set max date to today (can't select future dates)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        
        // Set min date to 120 years ago (reasonable limit)
        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.YEAR, -120)
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis

        datePickerDialog.show()
    }

    private fun calculateAndDisplayAge(year: Int, month: Int, day: Int) {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance().apply {
            set(year, month, day)
        }

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        // Check if birthday hasn't occurred yet this year
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        binding.tvAge.text = "Age: $age years"
        binding.tvAge.visibility = View.VISIBLE
    }

    private fun validateInputs(): Boolean {
        val phone = binding.etPhone.text.toString().trim()
        val dob = binding.etDob.text.toString().trim()
        val country = binding.etCountry.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val city = binding.etCity.text.toString().trim()

        when {
            phone.isEmpty() -> {
                showError("Please enter your phone number")
                binding.etPhone.requestFocus()
                return false
            }
            phone.length != 10 -> {
                showError("Please enter a valid 10-digit phone number")
                binding.etPhone.requestFocus()
                return false
            }
            dob.isEmpty() -> {
                showError("Please select your date of birth")
                return false
            }
            country.isEmpty() -> {
                showError("Please enter your country")
                binding.etCountry.requestFocus()
                return false
            }
            state.isEmpty() -> {
                showError("Please enter your state")
                binding.etState.requestFocus()
                return false
            }
            district.isEmpty() -> {
                showError("Please enter your district")
                binding.etDistrict.requestFocus()
                return false
            }
            city.isEmpty() -> {
                showError("Please enter your city")
                binding.etCity.requestFocus()
                return false
            }
        }

        return true
    }

    private fun saveUserInfo() {
        showLoading()

        val currentUser = AuthHelper.getCurrentUser()
        if (currentUser == null) {
            hideLoading()
            showError("User not authenticated")
            return
        }

        val phone = binding.etPhone.text.toString().trim()
        val gender = when (binding.rgGender.checkedRadioButtonId) {
            R.id.rb_male -> "Male"
            R.id.rb_female -> "Female"
            R.id.rb_other -> "Other"
            else -> "Male"
        }
        val dob = binding.etDob.text.toString().trim()
        val age = binding.tvAge.text.toString().replace("Age: ", "").replace(" years", "")
        val country = binding.etCountry.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val city = binding.etCity.text.toString().trim()

        // Create user profile object
        val userProfile = UserProfile(
            userId = currentUser.uid,
            name = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User",
            email = currentUser.email ?: "",
            phoneNumber = "+91$phone",
            gender = gender,
            dateOfBirth = dob,
            age = age,
            country = country,
            state = state,
            district = district,
            city = city
        )

        // Save to Firestore
        FirestoreHelper.saveUserProfile(
            userProfile = userProfile,
            onSuccess = {
                // Also save to local SharedPreferences for offline access
                FirestoreHelper.saveToLocalStorage(this, userProfile)
                
                hideLoading()
                android.widget.Toast.makeText(
                    this,
                    "Profile saved successfully!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                navigateToMainActivity()
            },
            onFailure = { exception ->
                hideLoading()
                showError("Failed to save profile: ${exception.message}")
            }
        )
    }

    private fun navigateToMainActivity() {
        // Check if permissions have been completed
        val sharedPrefs = getSharedPreferences("FraudGuardPrefs", MODE_PRIVATE)
        val permissionsCompleted = sharedPrefs.getBoolean("PERMISSIONS_COMPLETED", false)
        
        val intent = if (permissionsCompleted) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, PermissionsActivity::class.java)
        }
        
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun clearAllDataAndRestart() {
        // Clear all SharedPreferences
        getSharedPreferences("FraudGuardPrefs", MODE_PRIVATE).edit().clear().apply()
        
        // Sign out from Firebase
        AuthHelper.signOut(this)
        
        // Show confirmation
        android.widget.Toast.makeText(this, "All data cleared! Restarting...", android.widget.Toast.LENGTH_SHORT).show()
        
        // Restart app from login
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnContinue.isEnabled = false
        binding.btnContinue.text = ""
        
        binding.progressBar.alpha = 0f
        binding.progressBar.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    private fun hideLoading() {
        binding.progressBar.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.progressBar.visibility = View.GONE
                binding.btnContinue.isEnabled = true
                binding.btnContinue.text = "Continue"
            }
            .start()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun animateViews() {
        // Animate header
        binding.tvHeader.alpha = 0f
        binding.tvHeader.translationY = -30f
        binding.tvHeader.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .start()

        // Animate subheader
        binding.tvSubheader.alpha = 0f
        binding.tvSubheader.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        // Animate card
        binding.cardContainer.alpha = 0f
        binding.cardContainer.translationY = 50f
        binding.cardContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setStartDelay(300)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()

        // Animate skip button
        binding.tvSkip.alpha = 0f
        binding.tvSkip.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(500)
            .start()
    }
}
