package org.koitharu.kotatsu_dl.ui.screens.details

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.LocalKotatsuState
import org.koitharu.kotatsu_dl.data.model.ListModel
import org.koitharu.kotatsu_dl.ui.screens.main.MangaItem

@Composable
@Preview
fun SourceScreen(items: ListModel) {
	val state = LocalKotatsuState
	Column {
		Box {
			val lazyListState = rememberLazyGridState()
			Column {
				Text(items.toString(), modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.displayMedium)
				Box {
					LazyVerticalGrid(columns = GridCells.Adaptive(120.dp), Modifier.padding(end = 12.dp), lazyListState) {
						items(state.items) { items ->
							MangaItem(items)
						}
					}
					VerticalScrollbar(
						modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
						adapter = rememberScrollbarAdapter(lazyListState)
					)
				}
			}
		}
	}
}