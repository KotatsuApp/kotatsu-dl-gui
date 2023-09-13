package org.koitharu.kotatsu_dl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.data.Config
import org.koitharu.kotatsu_dl.data.Directories
import org.koitharu.kotatsu_dl.data.SourcesWithManga
import org.koitharu.kotatsu_dl.logic.KotatsuState
import org.koitharu.kotatsu_dl.ui.KotatsuTypography
import org.koitharu.kotatsu_dl.ui.state.TopBarProvider
import org.koitharu.kotatsu_dl.ui.state.TopBarState
import org.koitharu.kotatsu_dl.ui.rememberColorScheme
import org.koitharu.kotatsu_dl.ui.screens.Screens
import org.koitharu.kotatsu_dl.util.OS
import java.awt.Dimension

private val KotatsuStateProvider = compositionLocalOf<KotatsuState> { error("No local versions provided") }
val LocalKotatsuState: KotatsuState
	@Composable
	get() = KotatsuStateProvider.current

@OptIn(ExperimentalMaterial3Api::class)
fun main() {
	application {
		val windowState = rememberWindowState(placement = WindowPlacement.Floating)
		val kotatsuState by produceState<KotatsuState?>(null) {
			val config = Config.read()
			value = KotatsuState(
				config,
				SourcesWithManga.loadParsers(MangaSource.values().toList())
			)
		}
		val onClose: () -> Unit = {
			exitApplication()
			kotatsuState?.save()
		}
		Window(
			state = windowState,
			title = "kotatsu-dl",
			onCloseRequest = onClose,
			undecorated = true,
			transparent = OS.get() == OS.WINDOWS
		) {
			val topBarState = remember { TopBarState(onClose, windowState, this) }
			val ready = kotatsuState != null
			val hue = if (ready) kotatsuState?.hue else 0f
			val scheme = rememberColorScheme(hue?.div(100f) ?: 0f)
			window.minimumSize = Dimension(800, 600)
			MaterialTheme(colorScheme = scheme, typography = KotatsuTypography) {
				CompositionLocalProvider(TopBarProvider provides topBarState) {
					Surface(
						shape = RoundedCornerShape(14.dp)
					) {
						Scaffold {
							AnimatedVisibility(!ready, exit = fadeOut()) {
								Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
									CircularProgressIndicator()
								}
							}
							AnimatedVisibility(ready, enter = fadeIn()) {
								CompositionLocalProvider(
									KotatsuStateProvider provides kotatsuState!!,
								) {
									Directories.createDirs()
									Screens(items = kotatsuState!!.items[1])
								}
							}
						}
					}
				}
			}
		}
	}
}