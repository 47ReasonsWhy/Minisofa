package com.sofascoreacademy.minisofa.ui.tournament_details_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.attr.colorOnSurface
import com.google.android.material.R.attr.colorOnSurfaceVariant
import com.google.android.material.R.attr.colorSecondary
import com.google.android.material.R.attr.colorSurface
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.data.model.TournamentStanding
import com.sofascoreacademy.minisofa.databinding.ItemTournamentStandingBinding
import com.sofascoreacademy.minisofa.ui.util.getColorFromAttr
import com.sofascoreacademy.minisofa.ui.util.setTextColorFromAttr

class TournamentStandingsRecyclerAdapter(
    private val context: Context
) : RecyclerView.Adapter<TournamentStandingsRecyclerAdapter.TournamentStandingViewHolder>() {


    private var items = mutableListOf<TournamentStanding>()


    inner class TournamentStandingViewHolder(
        private val binding: ItemTournamentStandingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindHeader(sportId: Int) {
            binding.apply {
                mcvPosition.setCardBackgroundColor(context.getColorFromAttr(colorSurface))
                mcvPosition.cardElevation = 0f
                tvPosition.setTextColorFromAttr(colorOnSurfaceVariant)
                tvPosition.text = "#"
                tvTeamName.setTextColorFromAttr(colorOnSurfaceVariant)
                tvTeamName.text = context.getString(R.string.team)
                tvPlayed.setTextColorFromAttr(colorOnSurfaceVariant)
                tvPlayed.text = context.getString(R.string.played)
                tvWins.setTextColorFromAttr(colorOnSurfaceVariant)
                tvWins.text = context.getString(R.string.wins)
                tvLosses.setTextColorFromAttr(colorOnSurfaceVariant)
                tvLosses.text = context.getString(R.string.losses)

                when (sportId) {
                    1 -> {
                        tvDraws.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvDraws.text = context.getString(R.string.draws)
                        tvDraws.visibility = View.VISIBLE

                        tvGoals.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvGoals.text = context.getString(R.string.goals)
                        tvGoals.visibility = View.VISIBLE

                        tvPoints.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvPoints.text = context.getString(R.string.points)
                        tvPoints.visibility = View.VISIBLE

                        tvPercentage.visibility = View.GONE
                    }
                    2 -> {
                        tvDraws.visibility = View.GONE

                        tvGoals.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvGoals.text = context.getString(R.string.DIFF)
                        tvGoals.visibility = View.VISIBLE

                        tvPoints.visibility = View.GONE

                        tvPercentage.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvPercentage.text = context.getString(R.string.percentage)
                        tvPercentage.visibility = View.VISIBLE
                    }
                    3 -> {
                        tvDraws.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvDraws.text = context.getString(R.string.draws)
                        tvDraws.visibility = View.VISIBLE

                        tvGoals.visibility = View.GONE
                        tvPoints.visibility = View.GONE

                        tvPercentage.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvPercentage.text = context.getString(R.string.percentage)
                        tvPercentage.visibility = View.VISIBLE
                    }
                }
            }
        }

        fun bindItem(standing: TournamentStanding, position: Int) {
            binding.apply {
                mcvPosition.setCardBackgroundColor(context.getColorFromAttr(colorSecondary))
                mcvPosition.cardElevation = 4f
                tvPosition.setTextColorFromAttr(colorOnSurface)
                tvPosition.text = position.toString()
                tvTeamName.setTextColorFromAttr(colorOnSurface)
                tvTeamName.text = standing.team.name
                tvPlayed.setTextColorFromAttr(colorOnSurface)
                tvPlayed.text = standing.played.toString()
                tvWins.setTextColorFromAttr(colorOnSurface)
                tvWins.text = standing.wins.toString()
                tvLosses.setTextColorFromAttr(colorOnSurface)
                tvLosses.text = standing.losses.toString()
                when (standing.info.tournament.sport.id) {
                    1 -> {
                        tvDraws.setTextColorFromAttr(colorOnSurface)
                        tvDraws.text = standing.draws.toString()
                        tvDraws.visibility = View.VISIBLE
                        tvGoals.setTextColorFromAttr(colorOnSurface)
                        tvGoals.text = context.getString(R.string.goals_fb, standing.scoresFor, standing.scoresAgainst)
                        tvGoals.visibility = View.VISIBLE
                        tvPoints.setTextColorFromAttr(colorOnSurface)
                        tvPoints.text = standing.points?.toString() ?: ""
                        tvPoints.visibility = View.VISIBLE
                        tvPercentage.visibility = View.GONE
                    }
                    2 -> {
                        tvDraws.visibility = View.GONE
                        tvGoals.setTextColorFromAttr(colorOnSurface)
                        tvGoals.text = if (standing.played == 0) "–"
                                       else (standing.scoresFor - standing.scoresAgainst).toString()
                        tvGoals.visibility = View.VISIBLE
                        tvPoints.visibility = View.GONE
                        tvPercentage.setTextColorFromAttr(colorOnSurface)
                        tvPercentage.text = standing.percentage?.let {
                            context.getString(R.string.percentage_format, it)
                        } ?: ""
                    }
                    3 -> {
                        tvDraws.setTextColorFromAttr(colorOnSurface)
                        tvDraws.text = standing.draws.toString()
                        tvDraws.visibility = View.VISIBLE
                        tvGoals.visibility = View.GONE
                        tvPoints.visibility = View.GONE
                        tvPercentage.setTextColorFromAttr(colorOnSurface)
                        tvPercentage.text = standing.percentage?.let {
                            context.getString(R.string.percentage_format, it)
                        } ?: ""
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TournamentStandingViewHolder {
        val binding = ItemTournamentStandingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TournamentStandingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TournamentStandingViewHolder, position: Int) {
        if (position == 0) {
            holder.bindHeader(items[position].info.tournament.sport.id)
        } else {
            holder.bindItem(items[position], position)
        }
    }

    fun submitList(list: List<TournamentStanding>) {
        val diffUtilCallback = TournamentStandingDiffUtilCallback(items, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items.clear()
        items.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class TournamentStandingDiffUtilCallback(
        private val oldList: List<TournamentStanding>,
        private val newList: List<TournamentStanding>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}