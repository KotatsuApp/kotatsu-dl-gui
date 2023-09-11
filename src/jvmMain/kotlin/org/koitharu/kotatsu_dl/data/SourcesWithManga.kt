package org.koitharu.kotatsu_dl.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource


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
		suspend fun loadSources() = withContext(Dispatchers.IO) {
			return@withContext MangaSource.values()
		}
	}

}