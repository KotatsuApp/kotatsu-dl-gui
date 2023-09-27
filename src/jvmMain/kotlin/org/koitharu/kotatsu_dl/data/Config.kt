package org.koitharu.kotatsu_dl.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import kotlin.io.path.inputStream
import kotlin.io.path.writeText

@Serializable
data class Config(
	val lastSaveDir: String? = null,
	val colorScheme: ColorMode = ColorMode.SYSTEM,
) {

	private fun save() {
		Directories.configFile.writeText(YamlImpl.yaml.encodeToString(this))
	}

	companion object {

		private val stateFlow = MutableStateFlow(read())

		fun snapshot() = stateFlow.value

		val current: State<Config>
			@Composable
			get() = stateFlow.collectAsState()

		fun update(newConfig: Config) {
			newConfig.save()
			stateFlow.value = newConfig
		}

		private fun read() = runCatchingCancellable {
			YamlImpl.yaml.decodeFromStream(serializer(), Directories.configFile.inputStream())
		}.getOrDefault(Config())
	}
}