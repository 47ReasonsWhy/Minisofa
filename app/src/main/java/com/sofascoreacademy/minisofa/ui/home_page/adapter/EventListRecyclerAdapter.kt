package com.sofascoreacademy.minisofa.ui.home_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.R.attr.colorOnSurface
import com.google.android.material.R.attr.colorOnSurfaceVariant
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.R.attr.colorSpecificLive
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.data.model.enums.EventStatus
import com.sofascoreacademy.minisofa.data.model.enums.EventWinnerCode
import com.sofascoreacademy.minisofa.databinding.ItemEventHeaderBinding
import com.sofascoreacademy.minisofa.databinding.ItemEventItemBinding
import com.sofascoreacademy.minisofa.ui.util.setTextColorFromAttr

class EventListRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    sealed class EventListItem {
        data class EventItem(val event: Event, val onClick: (Event) -> Unit) : EventListItem()
        data class HeaderItem(val tournament: Tournament) : EventListItem()

        enum class ViewType {
            EVENT_ITEM,
            HEADER_ITEM
        }
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = mutableListOf<EventListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            EventListItem.ViewType.EVENT_ITEM.ordinal -> {
                val binding = ItemEventItemBinding.inflate(inflater, parent, false)
                EventItemViewHolder(binding)
            }
            EventListItem.ViewType.HEADER_ITEM.ordinal -> {
                val binding = ItemEventHeaderBinding.inflate(inflater, parent, false)
                HeaderItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EventListItem.EventItem -> (holder as EventItemViewHolder).bind(item.event, item.onClick)
            is EventListItem.HeaderItem -> (holder as HeaderItemViewHolder).bind(item.tournament)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EventListItem.EventItem -> EventListItem.ViewType.EVENT_ITEM.ordinal
            is EventListItem.HeaderItem -> EventListItem.ViewType.HEADER_ITEM.ordinal
        }
    }

    fun submitList(items: List<EventListItem>) {
        val diffUtilCallback = EventDiffUtilCallback(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        this.items.clear()
        this.items.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HeaderItemViewHolder(private val binding: ItemEventHeaderBinding) : ViewHolder(binding.root) {
        fun bind(tournament: Tournament) {
            binding.tvCountryName.text = tournament.country.name
            binding.tvLeagueName.text = tournament.name
            Glide.with(binding.root)
                .load(tournament.logoURL)
                .placeholder(R.drawable.ic_sofascore)
                .into(binding.ivTournamentLogo)
        }
    }

    inner class EventItemViewHolder(private val binding: ItemEventItemBinding) : ViewHolder(binding.root) {
        fun bind(event: Event, onClick: (Event) -> Unit) {
            binding.apply {
                tvStartTime.text = event.startTime
                when (event.status) {
                    EventStatus.NOT_STARTED -> {
                        tvCurrentTime.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvCurrentTime.text = "–"
                        tvHomeTeamScore.text = ""
                        tvAwayTeamScore.text = ""
                    }
                    EventStatus.IN_PROGRESS -> {
                        tvCurrentTime.setTextColorFromAttr(colorSpecificLive)
                        tvCurrentTime.text = context.getString(R.string.LIVE)
                        tvHomeTeamScore.text = event.homeScore.total.toString()
                        tvAwayTeamScore.text = event.awayScore.total.toString()
                    }
                    EventStatus.FINISHED -> {
                        tvCurrentTime.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvCurrentTime.text = context.getString(R.string.FT)
                        tvHomeTeamScore.text = event.homeScore.total.toString()
                        tvAwayTeamScore.text = event.awayScore.total.toString()
                    }
                }
                tvHomeTeamName.text = event.homeTeam.name
                tvAwayTeamName.text = event.awayTeam.name

                when (event.winnerCode) {
                    EventWinnerCode.HOME -> {
                        tvHomeTeamName.setTextColorFromAttr(colorOnSurface)
                        tvHomeTeamScore.setTextColorFromAttr(colorOnSurface)
                        tvAwayTeamName.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvAwayTeamScore.setTextColorFromAttr(colorOnSurfaceVariant)
                    }
                    EventWinnerCode.AWAY -> {
                        tvHomeTeamName.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvHomeTeamScore.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvAwayTeamName.setTextColorFromAttr(colorOnSurface)
                        tvAwayTeamScore.setTextColorFromAttr(colorOnSurface)
                    }
                    EventWinnerCode.DRAW -> {
                        tvHomeTeamName.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvHomeTeamScore.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvAwayTeamName.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvAwayTeamScore.setTextColorFromAttr(colorOnSurfaceVariant)
                    }
                    null -> {
                        tvHomeTeamName.setTextColorFromAttr(colorOnSurface)
                        tvHomeTeamScore.setTextColorFromAttr(colorOnSurface)
                        tvAwayTeamName.setTextColorFromAttr(colorOnSurface)
                        tvAwayTeamScore.setTextColorFromAttr(colorOnSurface)
                    }
                }
                if (event.status == EventStatus.IN_PROGRESS) {
                    tvHomeTeamScore.setTextColorFromAttr(colorSpecificLive)
                    tvAwayTeamScore.setTextColorFromAttr(colorSpecificLive)
                }

                Glide.with(binding.root)
                    .load(event.homeTeam.logoURL)
                    .placeholder(R.drawable.ic_sofascore)
                    .into(ivHomeTeamLogo)

                Glide.with(binding.root)
                    .load(event.awayTeam.logoURL)
                    .placeholder(R.drawable.ic_sofascore)
                    .into(ivAwayTeamLogo)

                root.setOnClickListener {
                    onClick(event)
                }
            }
        }
    }


    inner class EventDiffUtilCallback(
        private val oldList: List<EventListItem>,
        private val newList: List<EventListItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = when {
            oldList[oldItemPosition] is EventListItem.EventItem && newList[newItemPosition] is EventListItem.EventItem -> {
                (oldList[oldItemPosition] as EventListItem.EventItem).event.id == (newList[newItemPosition] as EventListItem.EventItem).event.id
            }
            oldList[oldItemPosition] is EventListItem.HeaderItem && newList[newItemPosition] is EventListItem.HeaderItem -> {
                (oldList[oldItemPosition] as EventListItem.HeaderItem).tournament.id == (newList[newItemPosition] as EventListItem.HeaderItem).tournament.id
            }
            else -> false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}