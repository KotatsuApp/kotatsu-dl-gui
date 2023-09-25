package org.koitharu.kotatsu_dl.logic

import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.data.Config
import org.koitharu.kotatsu_dl.data.model.ListModel
import org.koitharu.kotatsu_dl.ui.screens.list.MangaListWindow
import java.util.*

class KotatsuState(
	private val config: Config,
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

	val listWindows = mutableStateListOf<MangaListWindow>()

	fun openListWindow(source: MangaSource) {
		val state = WindowState(placement = WindowPlacement.Floating)
		val w = Array<MangaListWindow?>(1) { null }
		val onClose: () -> Unit = { listWindows.remove(w[0]) }
		w[0] = MangaListWindow(source, state, onClose)
		listWindows.add(w[0]!!)
	}
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())