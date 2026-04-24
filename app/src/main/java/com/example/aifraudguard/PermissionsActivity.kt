package com.example.aifraudguard

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.aifraudguard.databinding.ActivityPermissionsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding
    private var currentStep = 0

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                moveToNextStep()
            } else {
                showPermissionDeniedDialog()
            }
        }

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // After returning from settings, move to next step
            moveToNextStep()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        startPermissionFlow()
    }

    private fun setupViews() {
        binding.btnGrant.setOnClickListener {
            grantCurrentPermission()
        }

        binding.btnSkip.setOnClickListener {
            moveToNextStep()
        }
    }

    private fun startPermissionFlow() {
        currentStep = 0
        showCurrentPermission()
    }

    private fun showCurrentPermission() {
        when (currentStep) {
            0 -> showBasicPermissions()
            1 -> showCallerIdPermission()
            2 -> showOverlayPermission()
            3 -> showDialerPermission()
            4 -> showAccessibilityPermission()
            else -> finishPermissionFlow()
        }
    }

    private fun showBasicPermissions() {
        binding.tvTitle.text = "Basic Permissions"
        binding.tvDescription.text = "We need access to your phone, contacts, and microphone to protect you from scams."
        binding.ivIcon.setImageResource(R.drawable.ic_phone)
        binding.tvStep.text = "Step 1 of 5"
        updateProgress(20)
    }

    private fun showCallerIdPermission() {
        binding.tvTitle.text = "Enable Caller ID Service"
        binding.tvDescription.text = "This allows FraudGuard to identify incoming calls and warn you about potential scammers."
        binding.ivIcon.setImageResource(R.drawable.ic_phone)
        binding.tvStep.text = "Step 2 of 5"
        updateProgress(40)
    }

    private fun showOverlayPermission() {
        binding.tvTitle.text = "Grant Overlay Permission"
        binding.tvDescription.text = "This allows FraudGuard to show alerts on top of other apps during suspicious calls."
        binding.ivIcon.setImageResource(R.drawable.ic_shield)
        binding.tvStep.text = "Step 3 of 5"
        updateProgress(60)
    }

    private fun showDialerPermission() {
        binding.tvTitle.text = "Enable Call Management"
        binding.tvDescription.text = "This allows FraudGuard to manage calls and provide real-time protection."
        binding.ivIcon.setImageResource(R.drawable.ic_phone)
        binding.tvStep.text = "Step 4 of 5"
        updateProgress(80)
    }

    private fun showAccessibilityPermission() {
        binding.tvTitle.text = "Enable Message Scanning"
        binding.tvDescription.text = "This allows FraudGuard to scan messages in WhatsApp and Telegram for scam detection."
        binding.ivIcon.setImageResource(R.drawable.ic_email)
        binding.tvStep.text = "Step 5 of 5"
        updateProgress(100)
    }

    private fun grantCurrentPermission() {
        when (currentStep) {
            0 -> requestBasicPermissions()
            1 -> requestCallerIdPermission()
            2 -> requestOverlayPermission()
            3 -> requestDialerPermission()
            4 -> requestAccessibilityPermission()
        }
    }

    private fun requestBasicPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS
        )

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest)
        } else {
            moveToNextStep()
        }
    }

    private fun requestCallerIdPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                settingsLauncher.launch(intent)
            } else {
                moveToNextStep()
            }
        } else {
            moveToNextStep()
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            settingsLauncher.launch(intent)
        } else {
            moveToNextStep()
        }
    }

    private fun requestDialerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (!roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                settingsLauncher.launch(intent)
            } else {
                moveToNextStep()
            }
        } else {
            val intent = Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            settingsLauncher.launch(intent)
        }
    }

    private fun requestAccessibilityPermission() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Enable Accessibility Service")
            .setMessage("To scan messages for scams, please enable FraudGuard in Accessibility settings.\n\n1. Tap 'Open Settings'\n2. Find 'FraudGuard'\n3. Toggle it ON")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                settingsLauncher.launch(intent)
            }
            .setNegativeButton("Skip") { _, _ ->
                moveToNextStep()
            }
            .setCancelable(false)
            .show()
    }

    private fun moveToNextStep() {
        currentStep++
        if (currentStep <= 4) {
            animateTransition()
            showCurrentPermission()
        } else {
            finishPermissionFlow()
        }
    }

    private fun finishPermissionFlow() {
        // Mark permissions as completed
        getSharedPreferences("FraudGuardPrefs", MODE_PRIVATE)
            .edit()
            .putBoolean("PERMISSIONS_COMPLETED", true)
            .apply()

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Required")
            .setMessage("This permission is important for FraudGuard to protect you. You can skip for now, but some features may not work.")
            .setPositiveButton("Try Again") { _, _ ->
                grantCurrentPermission()
            }
            .setNegativeButton("Skip") { _, _ ->
                moveToNextStep()
            }
            .show()
    }

    private fun updateProgress(progress: Int) {
        binding.progressBar.progress = progress
    }

    private fun animateTransition() {
        binding.cardContainer.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.cardContainer.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }
}
