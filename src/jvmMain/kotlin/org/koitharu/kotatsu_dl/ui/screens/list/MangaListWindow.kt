package org.koitharu.kotatsu_dl.ui.screens.list

import androidx.compose.animation.core.tween
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.koitharu.kotatsu.parsers.util.toTitleCase
import org.koitharu.kotatsu_dl.C
import org.koitharu.kotatsu_dl.data.Config
import org.koitharu.kotatsu_dl.logic.downloader.LocalDownloadManager
import org.koitharu.kotatsu_dl.ui.*
import org.koitharu.kotatsu_dl.ui.screens.Window
import org.koitharu.kotatsu_dl.ui.screens.WindowManager
import org.koitharu.kotatsu_dl.util.ParsersFactory
import java.util.*

class MangaListWindow(
	private val state: WindowState,
	private val wm: WindowManager,
	private val onClose: () -> Unit,
) : Window {

	@Composable
	override operator fun invoke() {
		var currentSource by remember {
			mutableStateOf(
				Config.snapshot().lastSource?.let {
					MangaSource.entries.find { x -> x.name == it }
				} ?: ParsersFactory.all.first(),
			)
		}
		Window(
			state = state,
			title = currentSource.title + " - kotatsu-dl",
			onCloseRequest = onClose,
			icon = painterResource("icon4xs.png"),
			resizable = true,
		) {
			var error by rememberErrorHandler()
			var submittedQuery by rememberSaveable { mutableStateOf("") }
			val content = remember { mutableStateListOf<Manga>() }
			var isLoading by remember { mutableStateOf(true) }
			var offset by remember { mutableStateOf(0) }

			val parser = remember(currentSource) { ParsersFactory.createParser(currentSource) }

			Row(
				modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
			) {
				Column(
					modifier = Modifier.widthIn(min = 80.dp, max = 220.dp),
				) {
					NavTree(
						modifier = Modifier.fillMaxSize(),
						selectedSource = currentSource,
						onSourceSelected = {
							content.clear()
							offset = 0
							currentSource = it
							Config.update { c -> c.copy(lastSource = it.name) }
						},
					)
				}
				Box(
					modifier = Modifier
						.fillMaxHeight()
						.width(1.dp)
						.background(color = MaterialTheme.colorScheme.outlineVariant),
				)
				Column(
					modifier = Modifier.fillMaxHeight(),
				) {
					Toolbar(
						modifier = Modifier.fillMaxWidth(),
						onQuerySubmit = {
							content.clear()
							offset = 0
							submittedQuery = it
						},
					)
					LaunchedEffect(submittedQuery, offset, parser) {
						isLoading = true
						runCatchingCancellable {
							withContext(Dispatchers.Default) {
								parser.getList(offset, submittedQuery)
							}
						}.onSuccess {
							content.addAll(it)
						}.onFailure {
							error = it
						}
						isLoading = false
					}
					MangaList(
						content = content,
						isLoading = isLoading,
						onLoadMore = { offset = content.size },
					)
				}
			}
		}
	}

	@Composable
	private fun NavTree(
		modifier: Modifier,
		selectedSource: MangaSource,
		onSourceSelected: (MangaSource) -> Unit,
	) = Box(modifier) {
		val allSources = remember {
			ParsersFactory.all.groupBy { x -> x.locale }
		}
		val expandedLocales = remember { mutableStateMapOf(selectedSource.locale to true) }
		val listState = rememberLazyGridState()
		LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 2.dp)) {
			for ((lang, sources) in allSources) {
				item(contentType = Locale::class.java) {
					val locale = lang?.let { Locale(it) }
					Row(
						modifier = Modifier
							.clickable { expandedLocales[lang] = !expandedLocales.getOrDefault(lang, false) }
							.fillMaxWidth()
							.padding(horizontal = 6.dp, vertical = 4.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Text(
							modifier = Modifier.padding(end = 8.dp),
							text = locale.flagEmoji ?: "❓",
							fontFamily = NotoEmoji,
							maxLines = 1,
						)
						Text(
							text = locale?.getDisplayName(locale)?.toTitleCase(locale)
								?: LocalResources.current.string("locale_other"),
							maxLines = 1,
						)
					}
				}
				if (expandedLocales.getOrDefault(lang, false)) {
					this@LazyColumn.items(
						items = sources,
						contentType = { MangaSource::class.java },
					) { source ->
						SourceItem(
							modifier = Modifier.padding(start = 10.dp),
							source = source,
							isSelected = selectedSource == source,
							onClick = { if (source != selectedSource) onSourceSelected(source) },
						)
					}
				}
			}
		}
		VerticalScrollbar(
			modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
			adapter = rememberScrollbarAdapter(listState),
		)
	}

	@Composable
	private fun MangaList(
		content: SnapshotStateList<Manga>,
		isLoading: Boolean,
		onLoadMore: () -> Unit,
	) = Box(Modifier.fillMaxWidth().fillMaxHeight()) {
		val listState = rememberLazyGridState()
		when {
			content.isNotEmpty() -> {
				LazyVerticalGrid(
					modifier = Modifier.padding(horizontal = 6.dp),
					columns = GridCells.Adaptive(minSize = 142.dp),
					state = listState,
				) {
					items(content) { manga ->
						MangaCard(
							Modifier.padding(4.dp).clickable {
								wm.openDetailsWindow(manga)
							},
							manga,
						)
					}
				}
				InfiniteGridHandler(gridState = listState, onLoadMore = onLoadMore)
				VerticalScrollbar(
					modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
					adapter = rememberScrollbarAdapter(listState),
				)
			}

			isLoading -> {
				CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
			}

			else -> {
				Column(
					modifier = Modifier.align(Alignment.Center),
					verticalArrangement = Arrangement.SpaceAround,
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					Icon(
						imageVector = Icons.Default.Sailing,
						contentDescription = null,
					)
					Text(
						text = LocalResources.current.string("nothing_found"),
					)
				}
			}
		}
	}

	@Composable
	private fun MangaCard(modifier: Modifier, manga: Manga) = Card(modifier) {
		Column(
			modifier = Modifier.fillMaxWidth().padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
		) {
			Surface(
				shape = RoundedCornerShape(4.dp),
				modifier = Modifier.fillMaxWidth().aspectRatio(C.COVER_ASPECT_RATIO),
			) {
				MangaCover(manga)
			}
			Text(
				text = manga.title,
				style = MaterialTheme.typography.bodySmall,
				maxLines = 2,
				minLines = 2,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}

	@Composable
	fun SourceItem(
		modifier: Modifier,
		source: MangaSource,
		isSelected: Boolean,
		onClick: () -> Unit,
	) = Row(
		modifier = Modifier
			.background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
			.clickable(onClick = onClick)
			.fillMaxWidth()
			.padding(6.dp)
			.then(modifier),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
	) {
		KamelImage(
			modifier = Modifier.size(24.dp),
			resource = asyncPainterResource(source),
			contentDescription = source.title,
			onLoading = { _ -> IconProgressBox() },
			onFailure = { e ->
				Box(
					modifier = Modifier.fillMaxWidth().fillMaxHeight(),
					contentAlignment = Alignment.Center,
				) {
					Icon(
						imageVector = Icons.Default.Error,
						contentDescription = e.message,
					)
				}
			},
			animationSpec = tween(200),
		)
		Text(
			text = source.title,
			style = MaterialTheme.typography.bodyMedium,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
		)
	}

	@Composable
	private fun Toolbar(
		modifier: Modifier = Modifier,
		onQuerySubmit: (String) -> Unit,
	) = Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(2.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		var query by remember { mutableStateOf("") }

		BasicTextField(
			modifier = Modifier.weight(1f),
			value = query,
			onValueChange = { query = it },
			keyboardOptions = KeyboardOptions(
				capitalization = KeyboardCapitalization.Sentences,
				imeAction = ImeAction.Search,
			),
			keyboardActions = KeyboardActions(
				onSearch = { onQuerySubmit(query) },
			),
			singleLine = true,
			decorationBox = { innerTextField ->
				Row(
					modifier = Modifier
						.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(percent = 30))
						.padding(horizontal = 12.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					Box(
						modifier = Modifier.weight(1f),
						contentAlignment = Alignment.CenterStart,
					) {
						innerTextField()
						if (query.isEmpty()) {
							Text(
								text = LocalResources.current.string("search"),
								color = MaterialTheme.colorScheme.outline,
							)
						}
					}
					Spacer(Modifier.width(8.dp))
					IconButton(
						onClick = { onQuerySubmit(query) },
					) {
						Icon(
							imageVector = Icons.Default.ArrowForward,
							contentDescription = LocalResources.current.string("search"),
						)
					}
				}
			},
		)
		Spacer(modifier = Modifier.width(4.dp))
		val dm = LocalDownloadManager.current
		val totalProgress by dm.totalProgress.collectAsState()
		IconButton(
			onClick = {
				wm.showDownloadsWindow()
			},
		) {
			if (totalProgress in 0f..1f) {
				CircularProgressIndicator(
					progress = totalProgress,
					modifier = Modifier.fillMaxSize().padding(4.dp),
				)
			} else {
				Icon(
					imageVector = Icons.Default.Download,
					contentDescription = LocalResources.current.string("downloads"),
				)
			}
		}
		IconButton(
			onClick = {
				wm.showSettingsWindow()
			},
		) {
			Icon(
				imageVector = Icons.Default.Settings,
				contentDescription = LocalResources.current.string("settings"),
			)
		}
	}

}
