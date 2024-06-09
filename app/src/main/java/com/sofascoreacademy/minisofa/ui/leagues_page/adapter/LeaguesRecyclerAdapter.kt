package com.sofascoreacademy.minisofa.ui.leagues_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.databinding.ItemLeagueItemBinding

class LeaguesRecyclerAdapter(
    private val context: Context
) : RecyclerView.Adapter<LeaguesRecyclerAdapter.LeagueViewHolder>() {

    private var items = mutableListOf<LeagueItem>()

    data class LeagueItem(
        val tournament: Tournament,
        val onLeagueClick: (Tournament) -> Unit
    )

    inner class LeagueViewHolder(
        private val binding: ItemLeagueItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tournament: Tournament, onLeagueClick: (Tournament) -> Unit) {
            binding.apply {
                tvLeagueName.text = tournament.name
                Glide.with(context)
                    .load(tournament.logoURL)
                    .placeholder(R.drawable.ic_sofascore)
                    .into(ivLeagueLogo)
                root.setOnClickListener {
                    onLeagueClick(tournament)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val binding = ItemLeagueItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return LeagueViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item.tournament, item.onLeagueClick)
    }

    fun submitList(newList: List<LeagueItem>) {
        val diffUtilCallback = LeagueDiffUtilCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items.clear()
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class LeagueDiffUtilCallback(
        private val oldList: List<LeagueItem>,
        private val newList: List<LeagueItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].tournament.id == newList[newItemPosition].tournament.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}