package org.koitharu.kotatsu_dl.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState

val TopBarProvider = compositionLocalOf<TopBarState> { error("No top bar provided") }

val TopBar: TopBarState
	@Composable
	get() = TopBarProvider.current

val windowScope: WindowScope
	@Composable
	get() = TopBar.windowScope

class TopBarState(
	val onClose: () -> Unit,
	val windowState: WindowState,
	val windowScope: WindowScope,
) {
	fun toggleMaximized() {
		if (windowState.placement != WindowPlacement.Maximized)
			windowState.placement = WindowPlacement.Maximized
		else windowState.placement = WindowPlacement.Floating
	}
}