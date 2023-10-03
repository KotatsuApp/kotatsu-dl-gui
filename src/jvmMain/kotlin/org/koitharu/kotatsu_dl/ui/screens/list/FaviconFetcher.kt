package org.koitharu.kotatsu_dl.ui.screens.list

import io.kamel.core.DataSource
import io.kamel.core.Resource
import io.kamel.core.config.ResourceConfig
import io.kamel.core.fetcher.Fetcher
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.util.ParsersFactory

class FaviconFetcher(
	private val client: HttpClient,
) : Fetcher<MangaSource> {

	override val inputDataKClass = MangaSource::class
	override val source = DataSource.Network
	override val MangaSource.isSupported
		get() = true

	override fun fetch(data: MangaSource, resourceConfig: ResourceConfig): Flow<Resource<ByteReadChannel>> =
		channelFlow {
			send(Resource.Loading(0f))
			val parser = ParsersFactory.createParser(data)
			val favicons = parser.getFavicons()
			val icon = favicons.first()
			val response = client.request {
				onDownload { bytesSentTotal, contentLength ->
					val progress = (bytesSentTotal.toFloat() / contentLength).coerceIn(0F..1F)
						.takeUnless { it.isNaN() }
					if (progress != null) send(Resource.Loading(progress, source))
				}
				takeFrom(resourceConfig.requestData)
				url(icon.url)
			}
			val bytes = response.bodyAsChannel()
			send(Resource.Success(bytes, source))
		}
}