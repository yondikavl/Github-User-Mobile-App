package com.yondikavl.githubuser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.yondikavl.githubuser.data.ResponseGithub
import com.yondikavl.githubuser.databinding.RowUserBinding

class UserAdapter(private val onItemClick: (ResponseGithub.ItemItems) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userList: MutableList<ResponseGithub.ItemItems> = mutableListOf()

    fun setData(newDataList: List<ResponseGithub.ItemItems>) {
        val diffCallback = UserDiffCallback(userList, newDataList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        userList.clear()
        userList.addAll(newDataList)

        diffResult.dispatchUpdatesTo(this)
    }

    class UserViewHolder(private val binding: RowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userItem: ResponseGithub.ItemItems) {
            binding.ivUserAvatar.load(userItem.avatar_url) {
                transformations(CircleCropTransformation())
            }

            binding.tvUsername.text = userItem.login

            binding.tvUrl.text = userItem.html_url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(RowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int = userList.size
}

class UserDiffCallback(private val oldList: List<ResponseGithub.ItemItems>, private val newList: List<ResponseGithub.ItemItems>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
