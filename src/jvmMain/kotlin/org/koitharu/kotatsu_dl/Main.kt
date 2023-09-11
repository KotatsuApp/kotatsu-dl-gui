package org.koitharu.kotatsu_dl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koitharu.kotatsu_dl.ui.state.TopBarProvider
import org.koitharu.kotatsu_dl.ui.state.TopBarState
import org.koitharu.kotatsu_dl.ui.rememberColorScheme
import org.koitharu.kotatsu_dl.ui.screens.Screens
import java.awt.Dimension

@OptIn(ExperimentalMaterial3Api::class)
fun main() {
	application {
		val windowState = rememberWindowState(placement = WindowPlacement.Floating)
		val onClose: () -> Unit = {
			exitApplication()
		}
		Window(
			state = windowState,
			title = "kotatsu-dl",
			onCloseRequest = onClose,
			undecorated = true,
			transparent = true
		) {
			val topBarState = remember { TopBarState(onClose, windowState, this) }
			val scheme = rememberColorScheme(75 / 100F)
			window.minimumSize = Dimension(800, 600)
			MaterialTheme(colorScheme = scheme) {
				CompositionLocalProvider(TopBarProvider provides topBarState) {
					Surface(
						shape = RoundedCornerShape(14.dp)
					) {
						Scaffold {
							Screens()
						}
					}
				}
			}
		}
	}
}