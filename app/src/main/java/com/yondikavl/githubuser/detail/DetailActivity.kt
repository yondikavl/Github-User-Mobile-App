package com.yondikavl.githubuser.detail

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yondikavl.githubuser.R
import com.yondikavl.githubuser.data.local.DbModule
import com.yondikavl.githubuser.data.ResponseDetailUser
import com.yondikavl.githubuser.data.ResponseGithub
import com.yondikavl.githubuser.databinding.ActivityDetailBinding
import com.yondikavl.githubuser.Operation
import com.yondikavl.githubuser.UserAdapter
import com.yondikavl.githubuser.favorite.FavoriteViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModel.Factory(DbModule(this))
    }
    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        FavoriteViewModel.Factory(DbModule(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val githubResponseItem = intent.getParcelableExtra<ResponseGithub.ItemItems>("item")
        val username = githubResponseItem?.login ?: ""

        viewModel.resultDetailUser.observe(this) {
            when (it) {
                is Operation.Success<*> -> {
                    val user = it.data as ResponseDetailUser
                    binding.ivDetailAvatar.load(user.avatar_url) {
                        transformations(CircleCropTransformation())
                    }
                    binding.tvDetailName.text = user.login
                    binding.tvDetailUserName.text = user.name
                    binding.tvDetailUrl.text = user.html_url
                    binding.tvDetailFollowing.text = resources.getString(R.string.detail_following, user.following)
                    binding.tvDetailFollower.text = resources.getString(R.string.detail_follower, user.followers)
                    binding.tvDetailRepo.text = resources.getString(R.string.detail_repository, user.public_repos)

                    if (user.location.isNullOrEmpty()) {
                        binding.tvDetailLocation.text = getString(R.string.location_not_set)
                    } else {
                        binding.tvDetailLocation.text = user.location
                    }
                }
                is Operation.Error -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is Operation.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }
        viewModel.getDetailUser(username)

        val fragments = mutableListOf<Fragment>(
            FollowsFragment.newInstance(FollowsFragment.FOLLOWING),
            FollowsFragment.newInstance(FollowsFragment.FOLLOWERS)
                    )
        val titleFragments = mutableListOf(
            getString(R.string.following),getString(R.string.follower),
        )
        val detailAdapter = DetailAdapter(this, fragments)
        binding.viewPager.adapter = detailAdapter

        TabLayoutMediator(binding.tabPage, binding.viewPager) { tab, position ->
            tab.text = titleFragments[position]
        }.attach()

        binding.tabPage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    viewModel.getFollowing(username)
                } else {
                    viewModel.getFollowers(username)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.getFollowing(username)

        val adapter = UserAdapter { user ->
            Intent(this, DetailActivity::class.java).apply {
                putExtra("item", user)
                startActivity(this)
            }
        }

        viewModel.resultAddFavorite.observe(this) {
            if (it) {
                binding.btnFavorite.changeIconColor(R.color.gray_primary)
                favoriteViewModel.getUserFavorite().observe(this) { favorites ->
                    adapter.setData(favorites)
                }
            }
        }

        viewModel.resultRemoveFavorite.observe(this) {
            if (it) {
                binding.btnFavorite.changeIconColor(R.color.white)
                favoriteViewModel.getUserFavorite().observe(this) { favorites ->
                    adapter.setData(favorites)
                }
            }
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.setFavorite(githubResponseItem)
        }

        viewModel.findFavorite(githubResponseItem?.id ?: 0) {
            binding.btnFavorite.changeIconColor(R.color.gray_primary)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun FloatingActionButton.changeIconColor(@ColorRes color: Int) {
        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, color))
    }

    @Suppress("DEPRECATION")
    fun onShareClicked(view: View) {
        val dataUser = intent.getParcelableExtra<ResponseGithub.ItemItems>("item")!!
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Username: ${dataUser.login} \nURL: ${dataUser.html_url}"
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share to"))
    }

}
