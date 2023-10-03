package org.koitharu.kotatsu_dl.ui.screens.details

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.koitharu.kotatsu_dl.C
import org.koitharu.kotatsu_dl.logic.downloader.LocalDownloadManager
import org.koitharu.kotatsu_dl.ui.LocalResources
import org.koitharu.kotatsu_dl.ui.MangaCover
import org.koitharu.kotatsu_dl.ui.rememberErrorHandler
import org.koitharu.kotatsu_dl.ui.screens.Window
import org.koitharu.kotatsu_dl.ui.screens.WindowManager
import org.koitharu.kotatsu_dl.util.ParsersFactory
import java.awt.Desktop
import java.net.URI
import java.util.*

class DetailsWindow(
	private val state: WindowState,
	private val initialManga: Manga,
	private val wm: WindowManager,
) : Window {

	@OptIn(ExperimentalLayoutApi::class)
	@Composable
	override operator fun invoke() = Window(
		state = state,
		title = initialManga.title,
		onCloseRequest = { wm.close(this) },
		icon = painterResource("icon4xs.png"),
		resizable = true,
	) {
		var error by rememberErrorHandler()
		var manga by remember { mutableStateOf(initialManga) }
		LaunchedEffect(initialManga) {
			runCatchingCancellable {
				withContext(Dispatchers.Default) {
					ParsersFactory.createParser(initialManga.source).getDetails(initialManga)
				}
			}.onSuccess {
				manga = it
			}.onFailure {
				error = it
			}
		}
		Row(
			modifier = Modifier.background(MaterialTheme.colorScheme.background),
		) {
			Box {
				val scrollState = rememberScrollState()
				Column(
					modifier = Modifier
						.widthIn(min = 200.dp, max = 600.dp)
						.padding(8.dp)
						.fillMaxWidth(0.4f)
						.verticalScroll(scrollState),
				) {
					Surface(
						shape = RoundedCornerShape(4.dp),
						modifier = Modifier.fillMaxWidth().aspectRatio(C.COVER_ASPECT_RATIO),
					) {
						MangaCover(manga)
					}
					if (manga.tags.isNotEmpty()) {
						Spacer(Modifier.height(12.dp))
						FlowRow(
							horizontalArrangement = Arrangement.spacedBy(4.dp),
							modifier = Modifier.fillMaxWidth(),
						) {
							for (tag in manga.tags) {
								SuggestionChip(
									onClick = {},
									label = { Text(tag.title) },
								)
							}
						}
					}
					if (!manga.description.isNullOrBlank()) {
						Spacer(Modifier.height(12.dp))
						Text(manga.description.orEmpty())
					}
				}
				VerticalScrollbar(
					modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
					adapter = rememberScrollbarAdapter(scrollState),
				)
			}
			Box(
				modifier = Modifier
					.fillMaxHeight()
					.width(1.dp)
					.background(color = MaterialTheme.colorScheme.outlineVariant),
			)
			Column(
				modifier = Modifier
					.widthIn(min = 400.dp)
					.fillMaxWidth(),
			) {
				val branches = remember(manga.chapters.orEmpty()) {
					manga.chapters.orEmpty().mapToSet { it.branch }
				}
				var branch by remember(branches) {
					val localeName = Locale.getDefault().getDisplayLanguage(Locale.getDefault())
					mutableStateOf(
						branches.find { x ->
							x?.contains(localeName, ignoreCase = true) == true
						} ?: branches.firstOrNull(),
					)
				}
				val checkedChapters = remember(branch) {
					mutableStateMapOf<MangaChapter, Boolean>()
				}
				if (branches.size > 1) {
					val tabIndex = remember(branches, branch) { branches.indexOf(branch).coerceIn(branches.indices) }
					ScrollableTabRow(
						modifier = Modifier.fillMaxWidth(),
						selectedTabIndex = tabIndex,
					) {
						branches.forEachIndexed { index, b ->
							Tab(
								text = { Text(b ?: "Unknown") },
								selected = tabIndex == index,
								onClick = { branch = b },
							)
						}
					}
				}
				val chapters = remember(manga.chapters, branch) { manga.getChapters(branch).orEmpty() }
				Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
					val listState = rememberLazyListState()
					LazyColumn(
						modifier = Modifier.padding(4.dp),
						state = listState,
					) {
						items(chapters) { chapter ->
							Row(
								modifier = Modifier.clickable {
									checkedChapters[chapter] = checkedChapters[chapter] != true
								}.padding(
									horizontal = 8.dp,
									vertical = 4.dp,
								).fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically,
							) {
								Checkbox(
									checked = checkedChapters[chapter] == true,
									onCheckedChange = null,
								)
								Spacer(Modifier.width(8.dp))
								Text(
									text = chapter.name,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis,
								)
							}
						}
					}
					VerticalScrollbar(
						modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
						adapter = rememberScrollbarAdapter(listState),
					)
				}
				Divider(
					modifier = Modifier
						.fillMaxWidth(),
				)
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(8.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					val dm = LocalDownloadManager.current
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
						FilledTonalButton(
							onClick = {
								Desktop.getDesktop().browse(URI(manga.publicUrl))
							},
						) {
							Text(LocalResources.current.string("read"))
						}
					}
					Spacer(Modifier.weight(1f))
					FilledTonalButton(
						onClick = {
							checkedChapters.clear()
							chapters.associateWithTo(checkedChapters) { true }
						},
					) {
						Text(LocalResources.current.string("select_all"))
					}
					Button(
						onClick = {
							val chaptersIds = checkedChapters.mapNotNullTo(HashSet()) { (k, v) ->
								if (v) k.id else null
							}
							if (dm.startDownload(window, manga, chaptersIds)) {
								wm.showDownloadsWindow()
							}
						},
						enabled = checkedChapters.any { it.value },
					) {
						Text(LocalResources.current.string("download"))
					}
				}
			}
		}
	}
}