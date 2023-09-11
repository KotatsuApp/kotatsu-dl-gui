package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.ui.screens.Screen
import org.koitharu.kotatsu_dl.ui.screens.screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceItem(source: MangaSource) {

	Column {
		Card (
			modifier = Modifier.height(120.dp).padding(8.dp),
			onClick = { screen = Screen.Source }
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