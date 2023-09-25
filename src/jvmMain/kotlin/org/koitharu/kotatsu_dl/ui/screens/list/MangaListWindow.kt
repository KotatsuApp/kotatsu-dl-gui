package org.koitharu.kotatsu_dl.ui.screens.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import org.koitharu.kotatsu.parsers.model.MangaSource

class MangaListWindow(
	private val source: MangaSource,
	private val state: WindowState,
	private val onClose: () -> Unit,
) {

	@Composable
	operator fun invoke() = Window(
		state = state,
		title = source.title,
		onCloseRequest = onClose,
		icon = painterResource("icon4xs.png"),
		resizable = true,
	) {

	}
}