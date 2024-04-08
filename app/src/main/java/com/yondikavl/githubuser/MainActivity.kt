package com.yondikavl.githubuser

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yondikavl.githubuser.data.local.SettingPreferences
import com.yondikavl.githubuser.data.ResponseGithub
import com.yondikavl.githubuser.databinding.ActivityMainBinding
import com.yondikavl.githubuser.detail.DetailActivity
import com.yondikavl.githubuser.favorite.FavoriteActivity
import com.yondikavl.githubuser.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy {
        UserAdapter { user ->
            navigateToDetail(user)
        }
    }
    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(SettingPreferences(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeTheme()

        setupRecyclerView()

        setupSearchView()

        observeUserResult()

        viewModel.getGithubUser(DEFAULT_USERNAME)
    }

    private fun observeTheme() {
        viewModel.getTheme().observe(this) { isDarkMode ->
            val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.getGithubUser(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun observeUserResult() {
        viewModel.resultUser.observe(this) { result ->
            when (result) {
                is Operation.Success<*> -> {
                    val userList = result.data as? MutableList<ResponseGithub.ItemItems>
                    adapter.setData(userList ?: mutableListOf())
                }
                is Operation.Error -> {
                    Toast.makeText(this, result.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is Operation.Loading -> {
                    binding.progressBar.isVisible = result.isLoading
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemFavorite -> {
                startActivity(Intent(this, FavoriteActivity::class.java))
                true
            }
            R.id.itemSetting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToDetail(user: ResponseGithub.ItemItems) {
        Intent(this, DetailActivity::class.java).apply {
            putExtra("item", user)
            startActivity(this)
        }
    }

    companion object {
        private const val DEFAULT_USERNAME = "yondi"
    }
}
