package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import org.koitharu.kotatsu_dl.ui.InfoBar

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun MainScreen() {
	val coroutineScope = rememberCoroutineScope()
	Scaffold(
		bottomBar = { InfoBar() }
	) {

	}
}