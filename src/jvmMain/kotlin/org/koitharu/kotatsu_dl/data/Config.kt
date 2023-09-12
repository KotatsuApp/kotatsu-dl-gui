package org.koitharu.kotatsu_dl.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.path.inputStream
import kotlin.io.path.writeText

@Serializable
data class Config(
	val hue: Float? = null
) {

	fun save() {
		Directories.configFile.writeText(YamlImpl.yaml.encodeToString(this))
	}

	companion object {
		fun read() = runCatching {
			YamlImpl.yaml.decodeFromStream(serializer(), Directories.configFile.inputStream())
		}.getOrDefault(Config())
	}

}