package com.sofascoreacademy.minisofascore.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sofascoreacademy.minisofascore.data.local.entity.EventWithTeamsAndTournament
import com.sofascoreacademy.minisofascore.data.local.entity.Tournament
import com.sofascoreacademy.minisofascore.databinding.ItemEventHeaderBinding
import com.sofascoreacademy.minisofascore.databinding.ItemEventItemBinding

class EventsForSportAndDateRecyclerAdapter(context: Context) : RecyclerView.Adapter<ViewHolder>() {

    sealed class EventListItem {
        data class EventItem(val event: EventWithTeamsAndTournament) : EventListItem()
        data class HeaderItem(val tournament: Tournament) : EventListItem()

        enum class ViewType {
            EVENT_ITEM,
            HEADER_ITEM
        }
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var events = mutableListOf<EventListItem>()

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
        when (val event = events[position]) {
            is EventListItem.EventItem -> (holder as EventItemViewHolder).bind(event.event)
            is EventListItem.HeaderItem -> (holder as HeaderItemViewHolder).bind(event.tournament)
        }
    }

    override fun getItemCount() = events.size

    override fun getItemViewType(position: Int): Int {
        return when (events[position]) {
            is EventListItem.EventItem -> EventListItem.ViewType.EVENT_ITEM.ordinal
            is EventListItem.HeaderItem -> EventListItem.ViewType.HEADER_ITEM.ordinal
        }
    }

    fun submitList(events: List<EventListItem>) {
        val diffUtilCallback = EventDiffUtilCallback(this.events, events)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        this.events.clear()
        this.events.addAll(events)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HeaderItemViewHolder(private val binding: ItemEventHeaderBinding) : ViewHolder(binding.root) {
        fun bind(tournament: Tournament) {
            binding.tvCountryName.text = tournament.countryId.toString()
            binding.tvLeagueName.text = tournament.name
        }
    }

    inner class EventItemViewHolder(private val binding: ItemEventItemBinding) : ViewHolder(binding.root) {
        fun bind(event: EventWithTeamsAndTournament) {
            binding.apply {
                tvStartTime.text = event.event.startTime
                tvCurrentTime.text = event.event.round.toString()
                tvHomeTeamName.text = event.homeTeams[0].name
                tvAwayTeamName.text = event.awayTeams[0].name
                tvHomeTeamScore.text = event.event.homeScore.total.toString()
                tvAwayTeamScore.text = event.event.awayScore.total.toString()
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
                (oldList[oldItemPosition] as EventListItem.EventItem).event.event.id == (newList[newItemPosition] as EventListItem.EventItem).event.event.id
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