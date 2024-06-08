package com.sofascoreacademy.minisofa.ui.event_details_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.R.attr.colorOnSurfaceVariant
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.data.model.Incident
import com.sofascoreacademy.minisofa.data.model.enums.CardColor
import com.sofascoreacademy.minisofa.data.model.enums.GoalType
import com.sofascoreacademy.minisofa.data.model.enums.TeamSide
import com.sofascoreacademy.minisofa.databinding.ItemIncidentBasketballBinding
import com.sofascoreacademy.minisofa.databinding.ItemIncidentFootballBinding
import com.sofascoreacademy.minisofa.databinding.ItemIncidentPeriodBinding
import com.sofascoreacademy.minisofa.ui.util.getColorFromAttr

class IncidentListRecyclerAdapter (val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = mutableListOf<IncidentListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            IncidentListItem.ViewType.PERIOD.ordinal -> {
                val binding = ItemIncidentPeriodBinding.inflate(inflater, parent, false)
                IncidentItemPeriodViewHolder(binding)
            }
            IncidentListItem.ViewType.FOOTBALL.ordinal -> {
                val binding = ItemIncidentFootballBinding.inflate(inflater, parent, false)
                IncidentItemFootballViewHolder(binding)
            }
            IncidentListItem.ViewType.BASKETBALL.ordinal -> {
                val binding = ItemIncidentBasketballBinding.inflate(inflater, parent, false)
                IncidentItemBasketballViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = items[position]) {
            is IncidentListItem.IncidentItemPeriod -> (holder as IncidentItemPeriodViewHolder).bind(item.text)
            is IncidentListItem.IncidentItemFootball -> (holder as IncidentItemFootballViewHolder).bind(item.incident)
            is IncidentListItem.IncidentItemBasketball -> (holder as IncidentItemBasketballViewHolder).bind(item.incident)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is IncidentListItem.IncidentItemPeriod -> IncidentListItem.ViewType.PERIOD.ordinal
            is IncidentListItem.IncidentItemFootball -> IncidentListItem.ViewType.FOOTBALL.ordinal
            is IncidentListItem.IncidentItemBasketball -> IncidentListItem.ViewType.BASKETBALL.ordinal
        }
    }

    fun submitList(items: List<IncidentListItem>) {
        val diffUtilCallback = IncidentDiffUtilCallback(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        this.items.clear()
        this.items.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class IncidentItemPeriodViewHolder(private val binding: ItemIncidentPeriodBinding) : ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.tvEventDetailsHeader.text = text
        }
    }

    inner class IncidentItemFootballViewHolder(private val binding: ItemIncidentFootballBinding) : ViewHolder(binding.root) {
        fun bind(incident: Incident) {
            binding.apply {
                when (incident) {
                    is Incident.Card -> {
                        tvScoreHomeFb.visibility = View.GONE
                        tvScoreAwayFb.visibility = View.GONE
                        when (incident.teamSide) {
                            TeamSide.HOME -> {
                                ivIncidentIconAwayFb.visibility = View.GONE
                                tvTimeAwayFb.visibility = View.GONE
                                vSeparatorRightFb.visibility = View.GONE

                                when (incident.color) {
                                    CardColor.RED -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_card_red)
                                    else -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_card_yellow)
                                }
                                ivIncidentIconHomeFb.visibility = View.VISIBLE
                                tvTimeHome.text = context.getString(R.string.minute, incident.time.toString())
                                tvTimeHome.visibility = View.VISIBLE
                                vSeparatorLeftFb.visibility = View.VISIBLE

                                tvPlayerNameFb.text = incident.player.name
                                tvPlayerNameFb.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                            }
                            TeamSide.AWAY -> {
                                ivIncidentIconHomeFb.visibility = View.GONE
                                tvTimeHome.visibility = View.GONE
                                vSeparatorLeftFb.visibility = View.GONE

                                when (incident.color) {
                                    CardColor.RED -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_card_red)
                                    else -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_card_yellow)
                                }
                                ivIncidentIconAwayFb.visibility = View.VISIBLE
                                tvTimeAwayFb.text = context.getString(R.string.minute, incident.time.toString())
                                tvTimeAwayFb.visibility = View.VISIBLE
                                vSeparatorRightFb.visibility = View.VISIBLE

                                tvPlayerNameFb.text = incident.player.name
                                tvPlayerNameFb.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                            }
                        }
                    }

                    is Incident.Goal -> {
                        when (incident.scoringTeam) {
                            TeamSide.HOME -> {
                                ivIncidentIconAwayFb.visibility = View.GONE
                                tvTimeAwayFb.visibility = View.GONE
                                vSeparatorRightFb.visibility = View.GONE
                                tvScoreAwayFb.visibility = View.GONE

                                when (incident.goalType) {
                                    GoalType.REGULAR -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_ball_football)
                                    GoalType.OWN_GOAL -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_autogoal_football)
                                    GoalType.PENALTY -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_penalty_score)
                                    GoalType.TOUCHDOWN -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_touchdown)
                                    GoalType.SAFETY -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_two_point_conversion)
                                    GoalType.FIELD_GOAL -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_field_goal)
                                    GoalType.EXTRA_POINT -> ivIncidentIconHomeFb.setImageResource(R.drawable.ic_extra_point)
                                    else -> throw IllegalArgumentException("Invalid goal type for football: ${incident.goalType}")
                                }
                                tvTimeHome.text = context.getString(R.string.minute, incident.time.toString())
                                vSeparatorLeftFb.visibility = View.VISIBLE
                                tvScoreHomeFb.text = context.getString(R.string.score, incident.homeScore, incident.awayScore)
                                tvScoreHomeFb.visibility = View.VISIBLE

                                tvPlayerNameFb.text = incident.player.name
                                tvPlayerNameFb.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                            }
                            TeamSide.AWAY -> {
                                ivIncidentIconHomeFb.visibility = View.GONE
                                tvTimeHome.visibility = View.GONE
                                vSeparatorLeftFb.visibility = View.GONE
                                tvScoreHomeFb.visibility = View.GONE

                                when (incident.goalType) {
                                    GoalType.REGULAR -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_ball_football)
                                    GoalType.OWN_GOAL -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_autogoal_football)
                                    GoalType.PENALTY -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_penalty_score)
                                    GoalType.TOUCHDOWN -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_touchdown)
                                    GoalType.SAFETY -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_two_point_conversion)
                                    GoalType.FIELD_GOAL -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_field_goal)
                                    GoalType.EXTRA_POINT -> ivIncidentIconAwayFb.setImageResource(R.drawable.ic_extra_point)
                                    else -> throw IllegalArgumentException("Invalid goal type for football: ${incident.goalType}")
                                }
                                tvTimeAwayFb.text = context.getString(R.string.minute, incident.time.toString())
                                vSeparatorRightFb.visibility = View.VISIBLE
                                tvScoreAwayFb.text = context.getString(R.string.score, incident.homeScore, incident.awayScore)
                                tvScoreAwayFb.visibility = View.VISIBLE

                                tvPlayerNameFb.text = incident.player.name
                                tvPlayerNameFb.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                            }
                        }
                    }

                    else -> throw IllegalArgumentException("Invalid incident type for non-period football incident: ${incident.type}")
                }
            }
        }
    }

    inner class IncidentItemBasketballViewHolder(private val binding: ItemIncidentBasketballBinding) : ViewHolder(binding.root) {
        fun bind(incident: Incident) {
            when (incident) {
                is Incident.Goal -> binding.apply {
                    tvTimeBb.text = context.getString(R.string.minute, incident.time.toString())
                    when (incident.scoringTeam) {
                        TeamSide.HOME -> {
                            ivIncidentIconAwayBb.setImageDrawable(null)
                            vSeparatorRightBb.setBackgroundColor(context.getColor(R.color.transparent))
                            tvScoreAwayBb.text = ""

                            when (incident.goalType) {
                                GoalType.ONE_POINT -> ivIncidentIconHomeBb.setImageResource(R.drawable.ic_num_basketball_incident_1)
                                GoalType.TWO_POINT -> ivIncidentIconHomeBb.setImageResource(R.drawable.ic_num_basketball_incident_2)
                                GoalType.THREE_POINT -> ivIncidentIconHomeBb.setImageResource(R.drawable.ic_num_basketball_incident_3)
                                else -> throw IllegalArgumentException("Invalid goal type for basketball: ${incident.goalType}")
                            }
                            vSeparatorLeftBb.setBackgroundColor(getColorFromAttr(colorOnSurfaceVariant, context))
                            tvScoreHomeBb.text = context.getString(R.string.score, incident.homeScore, incident.awayScore)
                        }
                        TeamSide.AWAY -> {
                            ivIncidentIconHomeBb.setImageDrawable(null)
                            vSeparatorLeftBb.setBackgroundColor(context.getColor(R.color.transparent))
                            tvScoreHomeBb.text = ""

                            when (incident.goalType) {
                                GoalType.ONE_POINT -> ivIncidentIconAwayBb.setImageResource(R.drawable.ic_num_basketball_incident_1)
                                GoalType.TWO_POINT -> ivIncidentIconAwayBb.setImageResource(R.drawable.ic_num_basketball_incident_2)
                                GoalType.THREE_POINT -> ivIncidentIconAwayBb.setImageResource(R.drawable.ic_num_basketball_incident_3)
                                else -> throw IllegalArgumentException("Invalid goal type for basketball: ${incident.goalType}")
                            }
                            vSeparatorRightBb.setBackgroundColor(getColorFromAttr(colorOnSurfaceVariant, context))
                            tvScoreAwayBb.text = context.getString(R.string.score, incident.homeScore, incident.awayScore)
                        }
                    }
                }
            else -> throw IllegalArgumentException("Invalid incident type for basketball: ${incident.type}")
            }
        }
    }

    inner class IncidentDiffUtilCallback(
        private val oldList: List<IncidentListItem>,
        private val newList: List<IncidentListItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = when {
            oldList[oldItemPosition] is IncidentListItem.IncidentItemPeriod && newList[newItemPosition] is IncidentListItem.IncidentItemPeriod -> {
                (oldList[oldItemPosition] as IncidentListItem.IncidentItemPeriod).text == (newList[newItemPosition] as IncidentListItem.IncidentItemPeriod).text
            }
            oldList[oldItemPosition] is IncidentListItem.IncidentItemFootball && newList[newItemPosition] is IncidentListItem.IncidentItemFootball -> {
                (oldList[oldItemPosition] as IncidentListItem.IncidentItemFootball).incident == (newList[newItemPosition] as IncidentListItem.IncidentItemFootball).incident
            }
            oldList[oldItemPosition] is IncidentListItem.IncidentItemBasketball && newList[newItemPosition] is IncidentListItem.IncidentItemBasketball -> {
                (oldList[oldItemPosition] as IncidentListItem.IncidentItemBasketball).incident == (newList[newItemPosition] as IncidentListItem.IncidentItemBasketball).incident
            }
            else -> false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}