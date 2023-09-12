package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.ui.screens.Screen
import org.koitharu.kotatsu_dl.ui.screens.screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceItem(title: String) {

	Surface(
		modifier = Modifier.fillMaxWidth(),
		onClick = { screen = Screen.Source },
	) {
		Column {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Row(Modifier.weight(6f)) {
					Text(title, style = MaterialTheme.typography.bodyLarge)
				}
			}
		}
	}
}