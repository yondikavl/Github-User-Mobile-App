package com.yondikavl.githubuser.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yondikavl.githubuser.data.api.ApiClient
import com.yondikavl.githubuser.data.local.DbModule
import com.yondikavl.githubuser.data.ResponseGithub
import com.yondikavl.githubuser.Operation
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(private val dbModule: DbModule) : ViewModel() {
    val resultDetailUser = MutableLiveData<Operation>()
    val resultFollowersUser = MutableLiveData<Operation>()
    val resultFollowingUser = MutableLiveData<Operation>()
    val resultAddFavorite = MutableLiveData<Boolean>()
    val resultRemoveFavorite = MutableLiveData<Boolean>()

    private var isFavorite = false

    fun setFavorite(userItem: ResponseGithub.ItemItems?) {
        viewModelScope.launch {
            userItem?.let {
                if (isFavorite) {
                    dbModule.userDao.delete(userItem)
                    resultRemoveFavorite.value = true
                } else {
                    dbModule.userDao.insert(userItem)
                    resultAddFavorite.value = true
                }
                isFavorite = !isFavorite
            }
        }
    }

    fun findFavorite(id: Int, listenFavorite: () -> Unit) {
        viewModelScope.launch {
            val user = dbModule.userDao.findById(id)
            if (user != null) {
                listenFavorite()
                isFavorite = true
            }
        }
    }

    fun getDetailUser(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getDetailUserGithub(username)

                emit(response)
            }.onStart {
                resultDetailUser.value = Operation.Loading(true)
            }.onCompletion {
                resultDetailUser.value = Operation.Loading(false)
            }.catch {
                it.printStackTrace()
                resultDetailUser.value = Operation.Error(it)
            }.collect {
                resultDetailUser.value = Operation.Success(it)
            }
        }
    }

    fun getFollowers(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getFollowersUserGithub(username)

                emit(response)
            }.onStart {
                resultFollowersUser.value = Operation.Loading(true)
            }.onCompletion {
                resultFollowersUser.value = Operation.Loading(false)
            }.catch {
                it.printStackTrace()
                resultFollowersUser.value = Operation.Error(it)
            }.collect {
                resultFollowersUser.value = Operation.Success(it)
            }
        }
    }

    fun getFollowing(username: String) {
        viewModelScope.launch {
            flow {
                val response = ApiClient
                    .githubService
                    .getFollowingUserGithub(username)

                emit(response)
            }.onStart {
                resultFollowingUser.value = Operation.Loading(true)
            }.onCompletion {
                resultFollowingUser.value = Operation.Loading(false)
            }.catch {
                it.printStackTrace()
                resultFollowingUser.value = Operation.Error(it)
            }.collect {
                resultFollowingUser.value = Operation.Success(it)
            }
        }
    }

    class Factory(private val dbModule: DbModule) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(dbModule) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
