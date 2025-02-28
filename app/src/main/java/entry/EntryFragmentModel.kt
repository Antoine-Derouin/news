package entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feeds.FeedsRepository
import common.NewsApiSync
import db.Feed
import db.Entry
import entries.EntriesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.joda.time.Instant
import timber.log.Timber
import java.text.DateFormat
import java.util.*

class EntryFragmentModel(
    private val feedsRepository: FeedsRepository,
    private val entriesRepository: EntriesRepository,
    private val newsApiSync: NewsApiSync,
) : ViewModel() {

    suspend fun getFeed(id: String): Feed? {
        return feedsRepository.selectById(id)
    }

    suspend fun getEntry(id: String): Entry? {
        return entriesRepository.get(id).first()
    }

    fun getDate(entry: Entry): String {
        val instant = Instant.parse(entry.published)
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
            .format(Date(instant.millis))
    }

    suspend fun markAsOpened(entry: Entry) {
        if (entry.opened) {
            return
        }

        entriesRepository.setOpened(entry.id, true)
        syncEntriesFlags()
    }

    suspend fun getBookmarked(entry: Entry) = entriesRepository.get(entry.id).map { it?.bookmarked == true }

    suspend fun toggleBookmarked(entryId: String) {
        val entry = getEntry(entryId) ?: return
        entriesRepository.setBookmarked(entry.id, !entry.bookmarked)
        syncEntriesFlags()
    }

    private fun syncEntriesFlags() {
        viewModelScope.launch {
            runCatching {
                newsApiSync.syncEntriesFlags()
            }.onFailure {
                Timber.e(it)
            }
        }
    }
}