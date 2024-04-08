package com.yondikavl.githubuser.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yondikavl.githubuser.data.local.SettingPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val settingPreferences: SettingPreferences) : ViewModel() {
    fun getTheme() = settingPreferences.getThemeSetting().asLiveData()

    fun saveTheme(isDark: Boolean) {
        viewModelScope.launch {
            try {
                settingPreferences.saveThemeSetting(isDark)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val settingPreferences: SettingPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingViewModel(settingPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
