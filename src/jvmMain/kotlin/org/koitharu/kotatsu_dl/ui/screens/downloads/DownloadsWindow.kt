package org.koitharu.kotatsu_dl.ui.screens.downloads

import androidx.compose.animation.AnimatedContent
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.C
import org.koitharu.kotatsu_dl.logic.downloader.DownloadManager
import org.koitharu.kotatsu_dl.logic.downloader.DownloadState
import org.koitharu.kotatsu_dl.logic.downloader.LocalDownloadManager
import org.koitharu.kotatsu_dl.ui.LocalResources
import org.koitharu.kotatsu_dl.ui.MangaCover
import org.koitharu.kotatsu_dl.ui.screens.Window
import org.koitharu.kotatsu_dl.ui.screens.WindowManager
import java.awt.Desktop

class DownloadsWindow(
	private val state: WindowState,
	private val wm: WindowManager,
) : Window {

	@Composable
	override operator fun invoke() = Window(
		state = state,
		title = "Downloads",
		onCloseRequest = { wm.close(this) },
		icon = painterResource("icon4xs.png"),
		resizable = true,
	) {
		val dm = LocalDownloadManager.current
		Box(
			modifier = Modifier.background(MaterialTheme.colorScheme.background),
		) {
			val downloads by dm.state.collectAsState()
			if (downloads.isEmpty()) {
				Column(
					modifier = Modifier.align(Alignment.Center).fillMaxSize(),
					verticalArrangement = Arrangement.SpaceAround,
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					Icon(
						imageVector = Icons.Default.Sailing,
						contentDescription = null,
					)
					Text(
						text = LocalResources.current.string("no_downloads"),
					)
				}
			} else {
				val listState = rememberLazyListState()
				LazyColumn(
					modifier = Modifier.padding(4.dp),
					state = listState,
				) {
					items(downloads) { downloadState ->
						val state by downloadState.collectAsState()
						DownloadItem(state = state)
					}
				}
				VerticalScrollbar(
					modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
					adapter = rememberScrollbarAdapter(listState),
				)
			}
		}
	}

	@Composable
	private fun DownloadItem(modifier: Modifier = Modifier, state: DownloadState) {
		Card(
			modifier = Modifier.fillMaxWidth().padding(4.dp).then(modifier),
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(120.dp)
					.padding(8.dp),
				verticalAlignment = Alignment.Top,
			) {
				Surface(
					shape = RoundedCornerShape(4.dp),
					modifier = Modifier
						.fillMaxHeight()
						.aspectRatio(C.COVER_ASPECT_RATIO, true),
				) {
					MangaCover(state.manga)
				}
				Spacer(Modifier.width(8.dp))
				Column(
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(
						text = state.manga.title,
						style = MaterialTheme.typography.titleMedium,
					)
					AnimatedContent(
						targetState = state,
						contentKey = { it.javaClass },
					) {
						when (it) {
							is DownloadState.Done,
							is DownloadState.Error,
							is DownloadState.Cancelled,
							-> Unit

							is DownloadState.Queued,
							is DownloadState.Cancelling,
							is DownloadState.PostProcessing,
							is DownloadState.Preparing,
							-> {
								Spacer(modifier = Modifier.height(8.dp))
								LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
							}

							is DownloadState.Progress -> {
								Spacer(modifier = Modifier.height(8.dp))
								LinearProgressIndicator(
									modifier = Modifier.fillMaxWidth(),
									progress = it.percent,
								)
							}
						}
					}
					Spacer(Modifier.height(8.dp))
					Text(
						text = state.getTitle(),
						style = MaterialTheme.typography.bodyMedium,
					)
					Spacer(Modifier.heightIn(min = 4.dp).weight(1f))
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
					) {
						val dm = LocalDownloadManager.current
						if (state.isCancellable) {
							OutlinedButton(
								onClick = { dm.cancel(state.startId) },
							) {
								Text("Cancel")
							}
						}
						if (state is DownloadState.Done && Desktop.isDesktopSupported()) {
							FilledTonalButton(
								onClick = {
									val desktop = Desktop.getDesktop()
									if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
										desktop.browseFileDirectory(state.destination)
									} else if (desktop.isSupported(Desktop.Action.OPEN)) {
										desktop.open(state.destination)
									}
								},
							) {
								Text("Open")
							}
						}
					}
				}
			}
		}
	}

	@Preview
	@Composable
	fun DownloadItemPreview() {
		CompositionLocalProvider(LocalDownloadManager provides DownloadManager(rememberCoroutineScope())) {
			DownloadItem(
				modifier = Modifier.width(200.dp),
				state = DownloadState.Preparing(
					manga = Manga(
						id = 3595,
						title = "Wondering Emanon",
						altTitle = null,
						url = "http://www.bing.com/search?q=laoreet",
						publicUrl = "https://duckduckgo.com/?q=etiam",
						rating = 0.3f,
						isNsfw = false,
						coverUrl = "https://place-hold.it/200x300",
						tags = setOf(),
						state = null,
						author = null,
						largeCoverUrl = null,
						description = null,
						chapters = listOf(),
						source = MangaSource.DUMMY,
					),
					startId = 4759,
				),
			)
		}
	}

	private val DownloadState.isCancellable: Boolean
		get() {
			return this is DownloadState.Preparing || this is DownloadState.Queued || this is DownloadState.Progress
		}
}