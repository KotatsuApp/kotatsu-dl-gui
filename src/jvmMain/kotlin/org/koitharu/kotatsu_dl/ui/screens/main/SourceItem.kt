package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.core.config.ResourceConfigBuilder
import io.kamel.core.loadImageBitmapResource
import io.kamel.core.map
import io.kamel.image.KamelImage
import io.kamel.image.config.LocalKamelConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import org.koitharu.kotatsu_dl.ui.screens.Screen
import org.koitharu.kotatsu_dl.ui.screens.screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceItem(title: String) {

	val scope = rememberCoroutineScope()
	val resourceConfig = remember { ResourceConfigBuilder(parentScope = scope.coroutineContext).build() }
	val kamelConfig = LocalKamelConfig.current
	val imageBitmapResource by remember("https://www.redditstatic.com/avatars/defaults/v2/avatar_default_5.png") {
		kamelConfig.loadImageBitmapResource(
			"https://www.redditstatic.com/avatars/defaults/v2/avatar_default_5.png",
			resourceConfig,
		).map {
			delay(10L)
			it
		}
	}.collectAsState(Resource.Loading(0F), resourceConfig.coroutineContext)
	val painterResource = imageBitmapResource.map {
		remember(it) { BitmapPainter(it) }
	}

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
					KamelImage(
						painterResource,
						contentDescription = null,
						modifier = Modifier.width(56.dp).height(56.dp),
						onLoading = {
							LinearProgressIndicator(it, Modifier.align(Alignment.Center))
						},
						onFailure = { throw it },
					)
					Text(title, style = MaterialTheme.typography.bodyLarge)
				}
			}
		}
	}
}