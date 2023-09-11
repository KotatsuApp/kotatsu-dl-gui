package org.koitharu.kotatsu_dl.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.ui.AppTopBar
import org.koitharu.kotatsu_dl.ui.screens.main.MainScreen
import org.koitharu.kotatsu_dl.ui.state.TopBar

sealed class Screen(val title: String, val transparentTopBar: Boolean = false) {
	object Main : Screen("kotatsu-dl", transparentTopBar = true)
}

var screen: Screen by mutableStateOf(Screen.Main)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Screens() {
	TransitionFade(screen == Screen.Main) {
		MainScreen()
	}
	AppTopBar(
		TopBar,
		screen.transparentTopBar,
		showBackButton = screen != Screen.Main,
		onBackButtonClicked = { screen = Screen.Main }
	)
}

@Composable
fun TranslucentTopBar(currentScreen: Screen, content: @Composable () -> Unit) {
	Column {
		AnimatedVisibility(!currentScreen.transparentTopBar, enter = fadeIn(), exit = fadeOut()) {
			Spacer(Modifier.height(54.dp))
		}
		content()
	}
}

@Composable
fun TransitionFade(enabled: Boolean, content: @Composable () -> Unit) {
	AnimatedVisibility(enabled, enter = fadeIn(), exit = fadeOut()) {
		content()
	}
}