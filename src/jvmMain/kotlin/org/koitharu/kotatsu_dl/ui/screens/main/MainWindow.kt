package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.animation.core.tween
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.toTitleCase
import org.koitharu.kotatsu_dl.ui.NotoEmoji
import org.koitharu.kotatsu_dl.ui.flagEmoji
import org.koitharu.kotatsu_dl.ui.screens.WindowManager
import java.util.*

@Composable
fun MainWindow(
	state: WindowState,
	onClose: () -> Unit,
	wm: WindowManager,
) = Window(
	state = state,
	title = "kotatsu-dl",
	onCloseRequest = onClose,
	icon = painterResource("icon4xs.png"),
	resizable = true,
) {
	val sources = remember {
		MangaSource.entries.groupBy { x -> x.locale }
	}
	Column {
		Toolbar(
			modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxWidth(),
		)
		Row {
			var selectedLocale by remember { mutableStateOf(sources.keys.first()) }
			LazyColumn(
				modifier = Modifier.widthIn(min = 80.dp, max = 220.dp),
			) {
				items(sources.keys.toList()) { lang ->
					val locale = lang?.let { Locale(it) }
					val isSelected = lang == selectedLocale
					Row(
						modifier = Modifier.clickable(
							interactionSource = remember { MutableInteractionSource() },
							indication = rememberRipple(bounded = true),
						) { selectedLocale = lang }
							.background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
							.fillMaxWidth()
							.padding(8.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Text(
							modifier = Modifier.padding(end = 12.dp),
							text = locale.flagEmoji ?: "❓",
							fontFamily = NotoEmoji,
						)
						Text(text = locale?.getDisplayName(locale)?.toTitleCase(locale) ?: "Other")
					}
				}
			}
			Box {
				val listState = rememberLazyGridState()
				LazyVerticalGrid(
					modifier = Modifier.padding(4.dp),
					columns = GridCells.Adaptive(minSize = 82.dp),
					state = listState,
				) {
					items(sources[selectedLocale].orEmpty()) { source ->
						Card(
							modifier = Modifier.padding(4.dp).clickable {
								wm.openListWindow(source)
							},
						) {
							Column(
								modifier = Modifier.fillMaxWidth().padding(12.dp),
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
							) {
								Surface(
									shape = RoundedCornerShape(4.dp),
									modifier = Modifier.size(42.dp),
								) {
									KamelImage(
										resource = asyncPainterResource(source),
										contentDescription = source.title,
										onLoading = { progress ->
											Box(
												modifier = Modifier.fillMaxWidth().fillMaxHeight(),
												contentAlignment = Alignment.Center,
											) {
												CircularProgressIndicator(
													progress = progress,
													modifier = Modifier.align(Alignment.Center),
												)
											}
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
								Text(
									text = source.title,
									style = MaterialTheme.typography.bodyMedium,
									maxLines = 2,
									minLines = 2,
									overflow = TextOverflow.Ellipsis,
								)
							}
						}
					}
				}
				VerticalScrollbar(
					modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
					adapter = rememberScrollbarAdapter(listState),
				)
			}
		}
	}
}

@Composable
private fun Toolbar(
	modifier: Modifier = Modifier,
) = Row(
	modifier = modifier,
	horizontalArrangement = Arrangement.End,
	verticalAlignment = Alignment.CenterVertically,
) {
	IconButton(
		onClick = {},
	) {
		Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
	}
}