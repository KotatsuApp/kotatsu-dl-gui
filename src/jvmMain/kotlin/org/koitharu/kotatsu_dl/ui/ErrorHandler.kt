package org.koitharu.kotatsu_dl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow

@Composable
private fun ErrorDialog(
	error: Throwable?,
	onClearError: () -> Unit,
) {
	DialogWindow(
		visible = error != null,
		onCloseRequest = onClearError,
		title = LocalResources.current.string("error_occurred"),
		resizable = false,
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceBetween,
		) {
			Text(
				text = error?.localizedMessage.orEmpty(),
			)
			TextButton(
				onClick = onClearError,
			) {
				Text(LocalResources.current.string("close"))
			}
		}
	}
}

@Composable
fun rememberErrorHandler(): MutableState<Throwable?> {
	val state = remember { mutableStateOf<Throwable?>(null) }
	var error by state
	if (error != null) {
		ErrorDialog(error) { error = null }
	}
	return state
}