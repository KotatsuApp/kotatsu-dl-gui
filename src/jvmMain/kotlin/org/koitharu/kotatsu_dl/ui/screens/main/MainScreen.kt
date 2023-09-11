package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.LocalKotatsuState
import org.koitharu.kotatsu_dl.ui.InfoBar
import org.koitharu.kotatsu_dl.ui.screens.Screen
import org.koitharu.kotatsu_dl.ui.screens.screen

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun MainScreen() {
	val coroutineScope = rememberCoroutineScope()
	val state = LocalKotatsuState
	Scaffold(
		bottomBar = { InfoBar() }
	) {
		Row {
			NavigationRail {
				Image(
					painter = painterResource("icon_256.png"),
					contentDescription = "Kotatsu Logo",
					contentScale = ContentScale.FillWidth,
					modifier = Modifier.padding(16.dp).height(42.dp).width(42.dp)
				)
				NavigationRailItem(
					icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
					onClick = { screen = Screen.Settings },
					selected = false,
				)
			}
			Box {
				Card(
					elevation = CardDefaults.cardElevation(0.5.dp),
					shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
					modifier = Modifier.fillMaxSize(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {
					Box {
						val lazyGridState = rememberLazyGridState()
						Column {
							Text("Sources", modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.displayMedium)
							Box {
								LazyVerticalGrid(columns = GridCells.Adaptive(120.dp), Modifier.padding(end = 12.dp), lazyGridState) {
									items(state.sources) { sources ->
										SourceItem(sources)
									}
								}
								VerticalScrollbar(
									modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
									adapter = rememberScrollbarAdapter(lazyGridState)
								)
							}
						}
					}
				}
			}
		}
	}
}