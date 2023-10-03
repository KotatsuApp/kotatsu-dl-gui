@file:OptIn(ExperimentalMaterial3Api::class)

package org.koitharu.kotatsu_dl.ui.screens.list

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.koitharu.kotatsu_dl.ui.*
import org.koitharu.kotatsu_dl.ui.screens.Window
import org.koitharu.kotatsu_dl.ui.screens.WindowManager
import org.koitharu.kotatsu_dl.util.ParsersFactory
import kotlin.math.roundToInt

class MangaListWindow(
	private val source: MangaSource,
	private val state: WindowState,
	private val wm: WindowManager,
) : Window {

	private val parser = ParsersFactory.createParser(source)

	@Composable
	override operator fun invoke() = Window(
		state = state,
		title = source.title,
		onCloseRequest = { wm.close(this) },
		icon = painterResource("icon4xs.png"),
		resizable = true,
	) {
		var error by rememberErrorHandler()
		var query by remember { mutableStateOf("") }
		var submittedQuery by rememberSaveable { mutableStateOf("") }
		val content = remember { mutableStateListOf<Manga>() }
		var isLoading by remember { mutableStateOf(true) }
		val listState = rememberLazyGridState()
		var offset by remember { mutableStateOf(0) }
		val doSearch: () -> Unit = {
			content.clear()
			offset = 0
			submittedQuery = query
		}

		LaunchedEffect(submittedQuery, offset) {
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

		Column(
			modifier = Modifier.background(MaterialTheme.colorScheme.background),
		) {
			Row {
				BasicTextField(
					modifier = Modifier.widthIn(min = 220.dp),
					value = query,
					onValueChange = { query = it },
					keyboardOptions = KeyboardOptions(
						capitalization = KeyboardCapitalization.Sentences,
						imeAction = ImeAction.Search,
					),
					keyboardActions = KeyboardActions(
						onSearch = { doSearch() },
					),
					singleLine = true,
					decorationBox = { innerTextField ->
						Row(
							modifier = Modifier
								.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(percent = 30))
								.padding(horizontal = 16.dp),
							verticalAlignment = Alignment.CenterVertically,
						) {
							Box(contentAlignment = Alignment.CenterStart) {
								innerTextField()
								if (query.isEmpty()) {
									Text(
										text = LocalResources.current.string("search"),
										color = MaterialTheme.colorScheme.outline,
									)
								}
							}
							Spacer(Modifier.width(16.dp))
							IconButton(
								onClick = doSearch,
							) {
								Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Search")
							}
						}
					},
				)
			}
			Box(Modifier.fillMaxWidth().fillMaxHeight()) {
				when {
					content.isNotEmpty() -> {
						LazyVerticalGrid(
							modifier = Modifier.padding(4.dp),
							columns = GridCells.Adaptive(minSize = 128.dp),
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
						InfiniteGridHandler(gridState = listState) {
							offset = content.size
						}
						VerticalScrollbar(
							modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
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
								text = "Nothing found",
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun MangaCard(modifier: Modifier, manga: Manga) = Card(modifier) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
	) {
		Surface(
			shape = RoundedCornerShape(4.dp),
			modifier = Modifier.fillMaxWidth().aspectRatio(13f / 18f),
		) {
			Box {
				MangaCover(manga)
				if (manga.hasRating) {
					Surface(
						shape = RoundedCornerShape(4.dp),
						color = MaterialTheme.colorScheme.primaryContainer,
						modifier = Modifier.padding(4.dp)
							.align(Alignment.TopEnd),
					) {
						Text(
							text = "⭐" + (manga.rating * 5).roundToInt().toString(),
							fontFamily = NotoEmoji,
						)
					}
				}
			}
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
