package co.appreactor.nextcloud.news.feeditems

import co.appreactor.nextcloud.news.db.OpenGraphImage
import kotlinx.coroutines.flow.Flow

data class FeedItemsAdapterItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val unread: Boolean,
    val podcast: Boolean,
    val podcastDownloadPercent: Flow<Long?>,
    val image: Flow<OpenGraphImage?>,
    val showImage: Boolean,
    val cropImage: Boolean,
    val summary: Flow<String>,
    val cachedSummary: String?
)