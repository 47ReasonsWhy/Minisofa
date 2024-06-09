package com.sofascoreacademy.minisofa.ui.tournament_details_page

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sofascoreacademy.minisofa.data.remote.ITEMS_PER_PAGE
import com.sofascoreacademy.minisofa.data.remote.Result
import com.sofascoreacademy.minisofa.data.remote.response.EventResponse

/**
 * Assuming there are no more than 10 pages in both directions,
 * events will start from the first page of next events (at FIRST_PAGE)
 * Therefore:
 *      pages >= 0 will be for future events (pageKey = FIRST_PAGE -> 0, (pageKey = FIRST_PAGE + 1 -> 1, (pageKey = FIRST_PAGE + 2 -> 2, ...)
 *      pages < 0 will be for past events ((pageKey = FIRST_PAGE - 1 -> 0, (pageKey = FIRST_PAGE - 2 -> 1, (pageKey = FIRST_PAGE - 3 -> 2, ..
 */
class EventPagingSource(
    private val getLastEvents: suspend (Int) -> Result<List<EventResponse>>,
    private val getNextEvents: suspend (Int) -> Result<List<EventResponse>>
) : PagingSource<Int, EventResponse>() {

    companion object {
        const val FIRST_PAGE = 10
    }

    override fun getRefreshKey(state: PagingState<Int, EventResponse>): Int {
        return state.anchorPosition?.let { anchorPosition ->
            anchorPosition / ITEMS_PER_PAGE
        } ?: FIRST_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventResponse> {
        val pageKey = params.key ?: FIRST_PAGE
        if (pageKey >= FIRST_PAGE) return when (val events = getNextEvents(pageKey - FIRST_PAGE)) {
            is Result.Error -> LoadResult.Error(events.error)
            is Result.Success -> {
                LoadResult.Page(
                    data = events.data,
                    prevKey = (pageKey - 1),
                    nextKey = (pageKey + 1).takeIf { events.data.size == ITEMS_PER_PAGE },
                    itemsAfter = if (events.data.size == ITEMS_PER_PAGE) 1 else 0
                )
            }
        } else return when (val events = getLastEvents(FIRST_PAGE - pageKey - 1)) {
            is Result.Error -> LoadResult.Error(events.error)
            is Result.Success -> {
                LoadResult.Page(
                    data = events.data,
                    prevKey = (pageKey - 1).takeIf { events.data.size == ITEMS_PER_PAGE  && pageKey > 0 },
                    nextKey = (pageKey + 1),
                    itemsBefore = if (events.data.size == ITEMS_PER_PAGE) 1 else 0
                )
            }
        }
    }
}