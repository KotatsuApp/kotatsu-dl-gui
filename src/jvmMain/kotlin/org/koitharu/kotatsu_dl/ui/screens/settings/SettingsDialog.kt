package org.koitharu.kotatsu_dl.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import org.koitharu.kotatsu_dl.data.ColorMode
import org.koitharu.kotatsu_dl.data.Config
import org.koitharu.kotatsu_dl.ui.LocalResources
import org.koitharu.kotatsu_dl.ui.Spinner
import org.koitharu.kotatsu_dl.ui.screens.Window
import org.koitharu.kotatsu_dl.ui.screens.WindowManager

class SettingsDialog(
	private val state: DialogState,
	private val wm: WindowManager,
) : Window {

	@Composable
	override operator fun invoke() = DialogWindow(
		onCloseRequest = { wm.close(this) },
		state = state,
		title = LocalResources.current.string("settings"),
		icon = painterResource("icon4xs.png"),
		resizable = false,
	) {
		var config by remember { mutableStateOf(Config.snapshot()) }
		val resources = LocalResources.current
		Column(
			modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(8.dp),
		) {
			Text(
				text = resources.string("theme"),
				style = MaterialTheme.typography.bodyMedium,
			)
			Spacer(modifier = Modifier.height(2.dp))
			Spinner(
				selectedIndex = config.colorScheme.ordinal,
				onValueChanged = { i, _ ->
					val newValue = ColorMode.entries[i]
					config = config.copy(colorScheme = newValue)
				},
				items = listOf(
					resources.string("system_default"),
					resources.string("light"),
					resources.string("dark"),
				),
				style = MaterialTheme.typography.titleMedium,
			)
			Spacer(modifier = Modifier.weight(1f))
			Row(
				modifier = Modifier
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
				verticalAlignment = Alignment.CenterVertically,
			) {

				Button(
					onClick = { wm.close(this@SettingsDialog) },
				) {
					Text(resources.string("cancel"))
				}
				Button(
					onClick = {
						Config.update(config)
						wm.close(this@SettingsDialog)
					},
				) {
					Text(resources.string("apply"))
				}
			}
		}
	}
}
