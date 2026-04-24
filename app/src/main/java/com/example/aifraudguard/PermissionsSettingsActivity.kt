package com.example.aifraudguard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aifraudguard.databinding.ActivityPermissionsSettingsBinding

class PermissionsSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "App Permissions"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
