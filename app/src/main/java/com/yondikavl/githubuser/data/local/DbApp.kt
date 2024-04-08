package com.yondikavl.githubuser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yondikavl.githubuser.data.ResponseGithub

@Database(entities = [ResponseGithub.ItemItems::class], version = 1, exportSchema = false)
abstract class DbApp : RoomDatabase() {
    abstract fun userDao(): UserDao
}