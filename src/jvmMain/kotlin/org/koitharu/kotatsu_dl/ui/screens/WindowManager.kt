package org.koitharu.kotatsu_dl.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.ui.screens.details.DetailsWindow
import org.koitharu.kotatsu_dl.ui.screens.downloads.DownloadsWindow
import org.koitharu.kotatsu_dl.ui.screens.list.MangaListWindow

class WindowManager {

	private val windowsList = mutableStateListOf<Window>()

	fun openListWindow(source: MangaSource) {
		val state = WindowState(placement = WindowPlacement.Floating)
		windowsList.add(MangaListWindow(source, state, this))
	}

	fun openDetailsWindow(manga: Manga) {
		val state = WindowState(placement = WindowPlacement.Floating)
		windowsList.add(DetailsWindow(state, manga, this))
	}

	fun showDownloadsWindow() {
		if (windowsList.any { x -> x is DownloadsWindow }) {
			return
		}
		val state = WindowState(
			placement = WindowPlacement.Floating,
			size = DpSize(400.dp, 240.dp),
		)
		windowsList.add(DownloadsWindow(state, this))
	}

	fun close(window: Window) {
		windowsList.remove(window)
	}

	@Composable
	operator fun invoke() {
		windowsList.forEach {
			it.invoke()
		}
	}
}