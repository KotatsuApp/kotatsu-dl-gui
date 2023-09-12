package org.koitharu.kotatsu_dl.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koitharu.kotatsu.parsers.InternalParsersApi
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.data.model.ListModel
import org.koitharu.kotatsu_dl.data.model.MangaSourceItem
import org.koitharu.kotatsu_dl.logic.MangaLoaderContextImpl


@Serializable
data class SourcesWithManga(
	val sources: Set<MangaSource>,
	@SerialName("sourcesWithManga")
	private val _sourcesWithManga: Map<SourceName, Set<@Contextual Manga>>
) {

	val nameToSource: Map<SourceName, MangaSource> = sources.associateBy { it.title }

	@Transient
	val mangaSources: Map<MangaSource, Set<Manga>> = _sourcesWithManga.mapNotNull { nameToSource[it.key]?.to(it.value) }.toMap()
	val nameToManga: Map<SourceName, @Contextual Manga> = mangaSources.values
		.flatten()
		.associateBy { it.title }

	companion object {
		suspend fun loadParsers(sources: List<MangaSource>) : List<ListModel> = withContext(Dispatchers.IO) {
			val result = ArrayList<ListModel>(sources.size + 4)
			return@withContext sources.mapTo(result) { MangaSourceItem(it) }
		}
	}

}