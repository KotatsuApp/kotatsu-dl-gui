package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.ui.InfoBar
import org.koitharu.kotatsu_dl.ui.screens.Screen
import org.koitharu.kotatsu_dl.ui.screens.screen

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun MainScreen() {
	val coroutineScope = rememberCoroutineScope()
	Scaffold(
		bottomBar = { InfoBar() }
	) {
		Row {
			NavigationRail {
				Image(
					painter = painterResource("icon_256.png"),
					contentDescription = "Kotatsu Logo",
					contentScale = ContentScale.FillWidth,
					modifier = Modifier.padding(16.dp).height(42.dp).width(42.dp)
				)
				NavigationRailItem(
					icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
					onClick = { screen = Screen.Settings },
					selected = false,
				)
			}
			Box {
				Card(
					elevation = CardDefaults.cardElevation(0.5.dp),
					shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
					modifier = Modifier.fillMaxSize(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
				) {

				}
			}
		}
	}
}