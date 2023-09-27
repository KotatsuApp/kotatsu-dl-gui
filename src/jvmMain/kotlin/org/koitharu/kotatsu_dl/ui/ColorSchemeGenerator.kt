package org.koitharu.kotatsu_dl.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import com.jthemedetecor.OsThemeDetector
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import org.koitharu.kotatsu_dl.data.ColorMode
import org.koitharu.kotatsu_dl.data.Config
import java.util.function.Consumer

@Composable
fun rememberColorScheme(): ColorScheme {
	val config by Config.current
	val isDark by when (config.colorScheme) {
		ColorMode.SYSTEM -> observeSystemDarkTheme().collectAsState(OsThemeDetector.getDetector().isDark)
		ColorMode.LIGHT -> mutableStateOf(false)
		ColorMode.DARK -> mutableStateOf(true)
	}
	return remember(isDark) {
		if (isDark) {
			darkColorScheme()
		} else {
			lightColorScheme()
		}
	}
}

private fun observeSystemDarkTheme() = callbackFlow<Boolean> {
	val detector = OsThemeDetector.getDetector()
	val listener = Consumer<Boolean> {
		trySendBlocking(it)
	}
	detector.registerListener(listener)
	awaitClose {
		detector.removeListener(listener)
	}
}
