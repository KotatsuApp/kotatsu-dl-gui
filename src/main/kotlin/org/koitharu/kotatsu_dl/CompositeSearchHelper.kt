package org.koitharu.kotatsu_dl

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.levenshteinDistance
import org.koitharu.kotatsu_dl.util.ParsersFactory
import org.koitharu.kotatsu_dl.util.runCatchingCancellable

class CompositeSearchHelper(
	private val sources: List<MangaSource>,
	parallelism: Int,
) {

	private val semaphore = Semaphore(parallelism)

	suspend fun doSearch(query: String?): List<Manga> {
		if (query.isNullOrEmpty()) {
			return emptyList()
		}
		return coroutineScope {
			sources.map { source ->
				async { searchImpl(source, query) }
			}.awaitAll()
				.flatten()
				.sortedBy { query.levenshteinDistance(it.title) }
		}
	}

	private suspend fun searchImpl(source: MangaSource, query: String): List<Manga> {
		return semaphore.withPermit {
			runCatchingCancellable {
				ParsersFactory.create(source).getList(0, query)
			}.onFailure {
				it.printStackTrace()
			}.getOrDefault(emptyList())
		}
	}
}