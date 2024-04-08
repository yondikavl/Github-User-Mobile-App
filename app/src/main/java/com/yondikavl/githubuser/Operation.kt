package com.yondikavl.githubuser

sealed class Operation {
    data class Success<out T>(val data: T) : Operation()
    data class Error(val exception: Throwable) : Operation()
    data class Loading(val isLoading: Boolean) : Operation()
}
