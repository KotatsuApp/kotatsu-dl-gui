package org.koitharu.kotatsu_dl.logic

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.data.Config
import org.koitharu.kotatsu_dl.data.model.ListModel
import java.util.*

class KotatsuState(
	private val config: Config,
	val items: List<ListModel>
) {

	var hue by mutableStateOf(config.hue)

	val downloading = mutableStateMapOf<Manga, Progress>()
	val isDownloading by derivedStateOf { downloading.isNotEmpty() }
	val failedDownloads = mutableStateSetOf<Manga>()

	fun save() {
		config.copy(
			hue = hue
		).save()
	}

	private val remoteSources = EnumSet.allOf(MangaSource::class.java).apply {
		remove(MangaSource.LOCAL)
		remove(MangaSource.DUMMY)
	}

	val allMangaSources: Set<MangaSource>
		get() = Collections.unmodifiableSet(remoteSources)

}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())