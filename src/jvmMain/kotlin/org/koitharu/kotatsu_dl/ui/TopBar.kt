package org.koitharu.kotatsu_dl.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu_dl.ui.screens.screen
import org.koitharu.kotatsu_dl.ui.state.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WindowButton(icon: ImageVector, onClick: () -> Unit) {
	Surface(
		onClick = onClick,
		modifier = Modifier.fillMaxHeight().width(42.dp),
		contentColor = Color.White,
		color = Color.Transparent
	) {
		Icon(icon, "", Modifier.padding(10.dp))
	}
}

@Composable
fun AppTopBar(
	state: TopBarState,
	trasparent: Boolean,
	showBackButton: Boolean,
	onBackButtonClicked: (() -> Unit),
) = state.windowScope.WindowDraggableArea {
	Box(
		Modifier.fillMaxWidth().height(54.dp),
	) {
		AnimatedVisibility(
			!trasparent,
			enter = slideIn(initialOffset = { IntOffset(0, -54) }),
			exit = slideOut(targetOffset = { IntOffset(0, -54) })
		) {
			Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxSize()) {}
		}
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceAround,
		) {
			Spacer(Modifier.width(8.dp))
			Row(
				Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically,
			) {
				AnimatedVisibility(showBackButton,
					enter = slideIn(initialOffset = { IntOffset(0, -54) }),
					exit = slideOut(targetOffset = { IntOffset(0, -54) })
				) {
					IconButton(onClick = onBackButtonClicked) {
						Icon(Icons.Outlined.ArrowBack, contentDescription = "Back button")
					}
					Spacer(Modifier.width(56.dp))
				}
				AnimatedVisibility(!trasparent,
					enter = slideIn(initialOffset = { IntOffset(0, -54) }) + fadeIn(),
					exit = slideOut(targetOffset = { IntOffset(0, -54) }) + fadeOut()
				) {
					Text(
						text = screen.title,
						color = MaterialTheme.colorScheme.primary,
						style = MaterialTheme.typography.titleLarge
					)
				}
			}
			Row {
				WindowButton(Icons.Outlined.Minimize) {
					state.windowState.isMinimized = true
				}
				/*WindowButton(Icons.Outlined.CropSquare) {
					state.toggleMaximized()
				}*/
				WindowButton(Icons.Outlined.Close) {
					state.onClose()
				}
			}
		}
	}
}