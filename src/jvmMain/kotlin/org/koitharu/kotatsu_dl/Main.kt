package org.koitharu.kotatsu_dl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.batikSvgDecoder
import io.kamel.image.config.resourcesFetcher
import io.ktor.client.*
import org.koitharu.kotatsu_dl.data.Config
import org.koitharu.kotatsu_dl.logic.KotatsuState
import org.koitharu.kotatsu_dl.ui.screens.main.FaviconFetcher
import org.koitharu.kotatsu_dl.ui.screens.main.MainWindow

private val KotatsuStateProvider = compositionLocalOf<KotatsuState> { error("No local versions provided") }
val LocalKotatsuState: KotatsuState
	@Composable
	get() = KotatsuStateProvider.current

fun main() {
	application {
		val windowState = rememberWindowState(placement = WindowPlacement.Floating)
		val kotatsuState = KotatsuState(Config.read())
		val kamelConfig = KamelConfig {
			takeFrom(KamelConfig.Default)
			resourcesFetcher()
			batikSvgDecoder()
			fetcher(FaviconFetcher(HttpClient()))
		}
		CompositionLocalProvider(
			LocalKamelConfig provides kamelConfig,
			KotatsuStateProvider provides kotatsuState,
		) {
			MainWindow(
				state = windowState,
				onClose = {
					kotatsuState.save()
					exitApplication()
				},
			)
			LocalKotatsuState.listWindows.forEach {
				it.invoke()
			}
		}
	}
}