package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.Manga

@Composable
fun MangaItem(manga: Manga) {

	var expanded by remember { mutableStateOf(false) }
	val tonalElevation by animateDpAsState(if (expanded) 1.6.dp else 1.dp)

	Column {
		Card (
			modifier = Modifier.fillMaxWidth().height(120.dp).padding(8.dp),
			elevation = tonalElevation,
			backgroundColor = MaterialTheme.colorScheme.primaryContainer
		) {
			Text(
				manga.title, Modifier.weight(1f),
				style = MaterialTheme.typography.bodyLarge,
			)
		}
	}
}