package com.yondikavl.githubuser.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yondikavl.githubuser.data.ResponseGithub

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: ResponseGithub.ItemItems)

    @Query("SELECT * FROM User")
    fun loadAll(): LiveData<List<ResponseGithub.ItemItems>>

    @Query("SELECT * FROM User WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): ResponseGithub.ItemItems?

    @Delete
    fun delete(user: ResponseGithub.ItemItems)
}
