package com.yondikavl.githubuser.data.local

import android.content.Context
import androidx.room.Room

class DbModule(private val context: Context) {
    private val db = Room.databaseBuilder(context, DbApp::class.java, "githubuser.db")
        .allowMainThreadQueries()
        .build()

    val userDao = db.userDao()
}