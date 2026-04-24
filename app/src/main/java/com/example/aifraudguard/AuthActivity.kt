package com.example.aifraudguard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aifraudguard.databinding.ActivityAuthBinding
import com.google.android.material.snackbar.Snackbar

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var isSignUpMode = false

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            AuthHelper.handleGoogleSignInResult(
                task = task,
                onSuccess = { user ->
                    AuthHelper.saveFirebaseUserData(this, user)
                    navigateToMainActivity()
                },
                onFailure = { exception ->
                    showError("Google Sign-In failed: ${exception.message}")
                    hideLoading()
                }
            )
        } else {
            hideLoading()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is already signed in
        if (AuthHelper.isUserAuthenticated()) {
            navigateToMainActivity()
            return
        }

        setupViews()
        animateViews()
    }

    private fun animateViews() {
        // Animate shield icon
        binding.ivShield.alpha = 0f
        binding.ivShield.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(android.view.animation.OvershootInterpolator())
            .start()

        // Animate app name
        binding.tvAppName.alpha = 0f
        binding.tvAppName.translationY = -30f
        binding.tvAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        // Animate subtitle
        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(300)
            .start()

        // Animate card
        binding.cardContainer.alpha = 0f
        binding.cardContainer.translationY = 50f
        binding.cardContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setStartDelay(400)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()
    }

    private fun setupViews() {
        // Google Sign-In button
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        // Email/Password Sign-In button
        binding.btnEmailSignIn.setOnClickListener {
            if (isSignUpMode) {
                signUpWithEmail()
            } else {
                signInWithEmail()
            }
        }

        // Toggle between Sign In and Sign Up
        binding.tvToggleMode.setOnClickListener {
            toggleAuthMode()
        }
    }

    private fun signInWithGoogle() {
        showLoading()
        val signInIntent = AuthHelper.getGoogleSignInClient(this).signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signInWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateInput(email, password)) {
            return
        }

        showLoading()
        AuthHelper.signInWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                AuthHelper.saveFirebaseUserData(this, user)
                navigateToMainActivity()
            },
            onFailure = { exception ->
                val errorMessage = when {
                    exception.message?.contains("no user record", ignoreCase = true) == true ||
                    exception.message?.contains("invalid", ignoreCase = true) == true -> 
                        "Account not found. Please sign up first."
                    exception.message?.contains("password", ignoreCase = true) == true -> 
                        "Incorrect password. Please try again."
                    exception.message?.contains("network", ignoreCase = true) == true -> 
                        "Network error. Please check your connection."
                    else -> "Sign in failed: ${exception.message}"
                }
                showError(errorMessage)
                hideLoading()
            }
        )
    }

    private fun signUpWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateInput(email, password)) {
            return
        }

        if (password.length < 6) {
            showError("Password must be at least 6 characters")
            return
        }

        showLoading()
        AuthHelper.signUpWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                AuthHelper.saveFirebaseUserData(this, user)
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            },
            onFailure = { exception ->
                val errorMessage = when {
                    exception.message?.contains("already in use", ignoreCase = true) == true -> 
                        "This email is already registered. Please sign in instead."
                    exception.message?.contains("weak-password", ignoreCase = true) == true -> 
                        "Password is too weak. Use at least 6 characters."
                    exception.message?.contains("invalid-email", ignoreCase = true) == true -> 
                        "Invalid email format. Please check and try again."
                    exception.message?.contains("network", ignoreCase = true) == true -> 
                        "Network error. Please check your connection."
                    else -> "Sign up failed: ${exception.message}"
                }
                showError(errorMessage)
                hideLoading()
            }
        )
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            showError("Please enter your email")
            binding.etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address")
            binding.etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            showError("Please enter your password")
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun toggleAuthMode() {
        isSignUpMode = !isSignUpMode
        
        // Animate the transition
        binding.tvTitle.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                if (isSignUpMode) {
                    binding.btnEmailSignIn.text = "Sign Up"
                    binding.tvToggleMode.text = "Already have an account? Sign In"
                    binding.tvTitle.text = "Create Account"
                } else {
                    binding.btnEmailSignIn.text = "Sign In"
                    binding.tvToggleMode.text = "Don't have an account? Sign Up"
                    binding.tvTitle.text = "Welcome Back"
                }
                binding.tvTitle.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGoogleSignIn.isEnabled = false
        binding.btnEmailSignIn.isEnabled = false
        binding.btnEmailSignIn.text = ""
        
        // Animate progress bar
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
                binding.btnGoogleSignIn.isEnabled = true
                binding.btnEmailSignIn.isEnabled = true
                binding.btnEmailSignIn.text = if (isSignUpMode) "Sign Up" else "Sign In"
            }
            .start()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToMainActivity() {
        // Check if user has completed additional info
        val sharedPrefs = getSharedPreferences("FraudGuardPrefs", MODE_PRIVATE)
        val infoCompleted = sharedPrefs.getBoolean("USER_INFO_COMPLETED", false)
        
        val intent = if (infoCompleted) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, UserInfoActivity::class.java)
        }
        
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
