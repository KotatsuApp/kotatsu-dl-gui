package org.koitharu.kotatsu_dl.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
		Text(
			text = error?.localizedMessage.orEmpty(),
		)
		Button(
			onClick = onClearError,
		) {
			Text(LocalResources.current.string("close"))
		}
	}
}

@Composable
fun rememberErrorHandler(): MutableState<Throwable?> {
	val state = remember { mutableStateOf<Throwable?>(null) }
	var error by state
	ErrorDialog(error) { error = null }
	return state
}