package org.koitharu.kotatsu_dl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.LocalKotatsuState
import kotlin.random.Random

@Composable
fun InfoBar() {
	val state = LocalKotatsuState
	Surface(
		tonalElevation = 2.dp,
		shadowElevation = 0.dp,
		shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		LinearProgressIndicator(
			progress = Random.nextFloat(),
			modifier = Modifier.fillMaxWidth(),
			color = MaterialTheme.colorScheme.primaryContainer,
		)
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 6.dp),
		) {
			TextButton(onClick = { }) {
				Text("Ok", color = MaterialTheme.colorScheme.primary)
			}
			Spacer(Modifier.width(12.dp))
			Text("5 downloading")
		}
	}
}