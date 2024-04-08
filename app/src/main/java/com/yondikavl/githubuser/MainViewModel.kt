package com.yondikavl.githubuser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yondikavl.githubuser.data.api.ApiClient
import com.yondikavl.githubuser.data.local.SettingPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: SettingPreferences) : ViewModel() {

    val resultUser = MutableLiveData<Operation>()

    fun getTheme() = preferences.getThemeSetting().asLiveData()

    fun getGithubUser(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient.githubService.searchUserGithub(
                    mapOf(
                        "q" to username,
                        "per_page" to 15
                    )
                )
                emit(response)
            }.onStart {
                resultUser.value = Operation.Loading(true)
            }.onCompletion {
                resultUser.value = Operation.Loading(false)
            }.catch { exception ->
                exception.printStackTrace()
                resultUser.value = Operation.Error(exception)
            }.collect { githubResponse ->
                resultUser.value = Operation.Success(githubResponse.items)
            }
        }
    }

    class Factory(private val preferences: SettingPreferences) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(preferences) as T
    }
}
