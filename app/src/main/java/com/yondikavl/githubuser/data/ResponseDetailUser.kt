package com.yondikavl.githubuser.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseDetailUser(
    val avatar_url: String,
    val followers: Int,
    val following: Int,
    val html_url: String,
    val id: Int,
    val login: String,
    val name: String,
    val public_repos: Int,
    val type: String,
    val url: String,
    val location: String
) : Parcelable