package com.sofascoreacademy.minisofa.ui.tournament_details_page.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.R.attr.colorOnSurface
import com.google.android.material.R.attr.colorOnSurfaceVariant
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.enum.EventStatus
import com.sofascoreacademy.minisofa.data.model.enum.EventWinnerCode
import com.sofascoreacademy.minisofa.databinding.ItemEventItemBinding
import com.sofascoreacademy.minisofa.ui.home.adapter.EventListRecyclerAdapter.EventListItem.EventItem
import com.sofascoreacademy.minisofa.ui.util.setTextColorFromAttr
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TournamentMatchesPagingAdapter(
    private val context: Context,
    private val formatter: DateTimeFormatter,
) : PagingDataAdapter<EventItem, TournamentMatchesPagingAdapter.EventWithRoundViewHolder>(EventComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventWithRoundViewHolder {
        return EventWithRoundViewHolder(ItemEventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: EventWithRoundViewHolder, position: Int) {
        val eventItem = getItem(position)
        if (eventItem == null) holder.placeholder()
        else holder.bind(eventItem.event, eventItem.onClick)
    }

    inner class EventWithRoundViewHolder(private val binding: ItemEventItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, onClick: (Event) -> Unit) {
            binding.apply {
                ivEventPlaceholder.visibility = View.GONE
                vSeparator.visibility = View.VISIBLE
                // Show round only if it is different from the previous event
                if (bindingAdapterPosition in 1..<itemCount && getItem(bindingAdapterPosition - 1)?.event?.round == event.round) {
                    clEventRound.visibility = View.GONE
                    tvTournamentRound.text = ""
                } else {
                    clEventRound.visibility = View.VISIBLE
                    tvTournamentRound.text = context.getString(R.string.round_n, event.round)
                }

                // Fix round showing if we were at the start of the list but now we are below
                val positionNext = bindingAdapterPosition + 1
                if (positionNext < itemCount && getItem(positionNext)?.event?.round == event.round) {
                    // When the next view holder is available, check if the round is visible and hide it
                    queue.add {
                        val nextItemViewHolder = it.findViewHolderForAdapterPosition(bindingAdapterPosition + 1) as? EventWithRoundViewHolder
                        // If the next item has the round visible, redraw the next item to hide the round automatically
                        if (nextItemViewHolder?.binding?.clEventRound?.visibility == View.VISIBLE) {
                            Handler(Looper.getMainLooper()).post {
                                notifyItemChanged(bindingAdapterPosition + 1)
                            }
                        }
                    }
                }
                if (event.startDate != null) tvStartTime.text = formatter.format(LocalDate.parse(event.startDate))
                else tvStartTime.text = context.getString(R.string.TBA)
                when (event.status) {
                    EventStatus.NOT_STARTED -> {
                        tvCurrentTime.setTextColorFromAttr(colorOnSurfaceVariant)
                        tvCurrentTime.text = event.startTime ?: ""
                        tvHomeTeamScore.text = ""
                        tvAwayTeamScore.text = ""
                    }
                    EventStatus.IN_PROGRESS -> {
                        tvCurrentTime.setTextColorFromAttr(R.attr.colorSpecificLive)
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
                    tvHomeTeamScore.setTextColorFromAttr(R.attr.colorSpecificLive)
                    tvAwayTeamScore.setTextColorFromAttr(R.attr.colorSpecificLive)
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

        fun placeholder() {
            binding.apply {
                clEventRound.visibility = View.GONE
                vSeparator.visibility = View.GONE
                tvTournamentRound.text = ""
                tvStartTime.text = ""
                tvCurrentTime.text = ""
                tvHomeTeamName.text = ""
                tvAwayTeamName.text = ""
                tvHomeTeamScore.text = ""
                tvAwayTeamScore.text = ""
                ivHomeTeamLogo.setImageDrawable(null)
                ivAwayTeamLogo.setImageDrawable(null)
                ivEventPlaceholder.visibility = View.VISIBLE
            }
        }
    }

    object EventComparator : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.event.id == newItem.event.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem == newItem
        }
    }

    val queue = mutableListOf<(RecyclerView) -> Unit>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                queue.forEach { it(recyclerView) }
                queue.clear()
            }
            override fun onChildViewDetachedFromWindow(view: View) {}
        })
    }
}