package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaSource

@Composable
fun SourceItem(source: MangaSource) {

	var expanded by remember { mutableStateOf(false) }
	val tonalElevation by animateDpAsState(if (expanded) 1.6.dp else 1.dp)

	Column {
		Card (
			modifier = Modifier.height(120.dp).padding(8.dp),
		) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Text(
					source.title,
					Modifier.fillMaxSize(),
					style = MaterialTheme.typography.bodyLarge,
					textAlign = TextAlign.Center
				)
			}
		}
	}
}