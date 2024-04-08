package com.yondikavl.githubuser.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yondikavl.githubuser.R
import com.yondikavl.githubuser.data.local.SettingPreferences
import com.yondikavl.githubuser.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel by viewModels<SettingViewModel> {
        SettingViewModel.Factory(SettingPreferences(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        settingViewModel.getTheme().observe(this) { isDarkTheme ->
            updateThemeUI(isDarkTheme)
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveTheme(isChecked)
        }
    }

    private fun updateThemeUI(isDarkTheme: Boolean) {
        val themeText = getString(if (isDarkTheme) R.string.dark_theme else R.string.light_theme)
        binding.switchTheme.text = themeText
        val nightMode = if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)
        binding.switchTheme.isChecked = isDarkTheme
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
