package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.data.model.ListModel
import org.koitharu.kotatsu_dl.data.model.MangaSourceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceGroupItem(items: ListModel) {

	var expanded by remember { mutableStateOf(false) }
	val tonalElevation by animateDpAsState(if (expanded) 1.6.dp else 1.dp)
	val arrowRotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

	Column {
		Surface(
			tonalElevation = tonalElevation,
			shape = RoundedCornerShape(20.dp),
			modifier = Modifier.fillMaxWidth(),
			onClick = { expanded = !expanded },
		) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Spacer(Modifier.width(10.dp))
				Text(
					items.toString(), Modifier.fillMaxWidth(),
					style = MaterialTheme.typography.bodyLarge,
				)
				Spacer(Modifier.fillMaxWidth())
				Icon(Icons.Outlined.ArrowDropDown, "Show sources", Modifier.rotate(arrowRotationState))
				Spacer(Modifier.width(10.dp))
			}
		}
		AnimatedVisibility(expanded) {
			Surface(
				tonalElevation = 0.2.dp,
				shape = RoundedCornerShape(20.dp),
				modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp, start = 10.dp)
			) {
				Column {
					SourceItem(items.toString())
				}
			}
		}
	}
}