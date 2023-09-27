package org.koitharu.kotatsu_dl.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.*
import io.ktor.client.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koitharu.kotatsu.parsers.model.Manga

@Composable
fun CircularProgressBox(progress: Float) {
	Box(
		modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(4.dp),
		contentAlignment = Alignment.Center,
	) {
		AnimatedContent(
			targetState = progress,
			contentKey = { it <= 0f },
		) {
			if (it <= 0f) {
				CircularProgressIndicator(
					modifier = Modifier.align(Alignment.Center),
				)
			} else {
				CircularProgressIndicator(
					progress = it,
					modifier = Modifier.align(Alignment.Center),
				)
			}
		}
	}
}

@Composable
fun IconProgressBox() {
	Box(
		modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(4.dp),
		contentAlignment = Alignment.Center,
	) {
		Icon(
			imageVector = Icons.Default.HourglassBottom,
			contentDescription = "Loading",
			tint = MaterialTheme.colorScheme.outline,
		)
	}
}

@Composable
fun InfiniteListHandler(
	listState: LazyListState,
	buffer: Int = 2,
	onLoadMore: () -> Unit,
) {
	val loadMore = remember {
		derivedStateOf {
			val layoutInfo = listState.layoutInfo
			val totalItemsNumber = layoutInfo.totalItemsCount
			val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

			lastVisibleItemIndex > (totalItemsNumber - buffer)
		}
	}

	LaunchedEffect(loadMore) {
		snapshotFlow { loadMore.value }
			.distinctUntilChanged()
			.collect {
				if (it) {
					onLoadMore()
				}
			}
	}
}

@Composable
fun InfiniteGridHandler(
	gridState: LazyGridState,
	buffer: Int = 2,
	onLoadMore: () -> Unit,
) {
	val loadMore = remember {
		derivedStateOf {
			val layoutInfo = gridState.layoutInfo
			val totalItemsNumber = layoutInfo.totalItemsCount
			val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

			lastVisibleItemIndex > (totalItemsNumber - buffer)
		}
	}

	LaunchedEffect(loadMore) {
		snapshotFlow { loadMore.value }
			.distinctUntilChanged()
			.collect {
				if (it) {
					onLoadMore()
				}
			}
	}
}

/*
@Composable
fun Spinner(
	text: String,
	onValueChanged: (String) -> Unit,
	label: String,
	items: List<String>,
) {
	var isOpened by remember { mutableStateOf(false) }
	Box {
		Column {
			OutlinedTextField(
				value = text,
				onValueChange = onValueChanged,
				label = { Text(text = label) },
				modifier = Modifier.fillMaxWidth()
			)
			DropdownMenu(
				expanded = isOpened,
				onDismissRequest = { isOpened = false },
			) {
				items.forEach {
					DropdownMenuItem(
						text = { Text(it) },
						modifier = Modifier.fillMaxWidth(),
						onClick = { onValueChanged(it) }
					) {
						Text(it, modifier = Modifier.wrapContentWidth().align(Alignment.Start))
					}
				}
			}
		}
		Spacer(
			modifier = Modifier.matchParentSize().background(Color.Transparent).padding(10.dp)
				.clickable(
					onClick = { isOpened = true }
				)
		)
	}
}*/

@Composable
fun MangaCover(
	manga: Manga,
	modifier: Modifier = Modifier,
) {
	KamelImage(
		modifier = modifier,
		resource = asyncPainterResource(manga.largeCoverUrl?.takeUnless { it.isBlank() } ?: manga.coverUrl) {
			coroutineContext += Dispatchers.IO
			requestBuilder { // this: HttpRequestBuilder
				parameter("source", manga.source)
				cacheControl(CacheControl.MAX_AGE)
			}
		},
		contentScale = ContentScale.Crop,
		contentDescription = manga.title,
		onLoading = { _ ->
			IconProgressBox()
		},
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
}