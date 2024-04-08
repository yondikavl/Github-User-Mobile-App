package com.yondikavl.githubuser.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

data class ResponseGithub(
    val incomplete_results: Boolean,
    val items: MutableList<ItemItems>,
    val total_count: Int
) {
    @Parcelize
    @Entity(tableName = "user")
    data class ItemItems(
        @ColumnInfo(name = "avatar_url")
        val avatar_url: String,
        @PrimaryKey
        val id: Int,
        @ColumnInfo(name = "login")
        val login: String,
        @ColumnInfo(name = "html_url")
        val html_url: String,
    ) : Parcelable
}