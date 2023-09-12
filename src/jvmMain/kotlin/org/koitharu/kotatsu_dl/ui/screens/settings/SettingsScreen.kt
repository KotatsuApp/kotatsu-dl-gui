package org.koitharu.kotatsu_dl.ui.screens.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.LocalKotatsuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SettingsScreen() {
	val state = LocalKotatsuState
	Scaffold { paddingValues ->
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Surface(
				shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
				modifier = Modifier.padding(5.dp)
			) {
				Box(
					Modifier.padding(paddingValues).padding(start = 10.dp, top = 54.dp)
				) {
					val lazyListState = rememberLazyListState()
					LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
						item("java settings") {
							val minRam = SliderSwitch(valueRange = 1..10)
							// Figure out way to handle this, probably storing via state or something
							state.hue = minRam
						}
					}
					VerticalScrollbar(
						modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
						adapter = rememberScrollbarAdapter(lazyListState)
					)
				}
			}
		}
	}
}