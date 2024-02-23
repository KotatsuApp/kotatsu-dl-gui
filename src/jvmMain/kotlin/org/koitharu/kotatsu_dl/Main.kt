package org.koitharu.kotatsu_dl

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import org.koitharu.kotatsu_dl.data.Directories
import org.koitharu.kotatsu_dl.logic.downloader.DownloadManager
import org.koitharu.kotatsu_dl.logic.downloader.LocalDownloadManager
import org.koitharu.kotatsu_dl.ui.KotatsuTypography
import org.koitharu.kotatsu_dl.ui.LocalResources
import org.koitharu.kotatsu_dl.ui.Resources
import org.koitharu.kotatsu_dl.ui.rememberColorScheme
import org.koitharu.kotatsu_dl.ui.screens.WindowManager
import org.koitharu.kotatsu_dl.ui.screens.list.MangaListWindow
import org.koitharu.kotatsu_dl.ui.screens.list.FaviconFetcher

fun main() {
	Directories.createDirs()
	application {
		val kamelConfig = KamelConfig {
			takeFrom(KamelConfig.Default)
			resourcesFetcher()
			batikSvgDecoder()
			fetcher(FaviconFetcher(HttpClient()))
		}
		val wm = remember { WindowManager() }
		val appCoroutineScope = rememberCoroutineScope()
		val downloadManager = remember(appCoroutineScope) { DownloadManager(appCoroutineScope) }
		MaterialTheme(colorScheme = rememberColorScheme(), typography = KotatsuTypography) {
			CompositionLocalProvider(
				LocalKamelConfig provides kamelConfig,
				LocalDownloadManager provides downloadManager,
				LocalContentColor provides MaterialTheme.colorScheme.onBackground,
				LocalResources provides remember { Resources() },
			) {
				MangaListWindow(
					state = rememberWindowState(placement = WindowPlacement.Floating),
					onClose = ::exitApplication,
					wm = wm,
				).invoke()
				wm()
			}
		}

	}
}