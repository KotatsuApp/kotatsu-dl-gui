package org.koitharu.kotatsu_dl.data

import org.koitharu.kotatsu_dl.util.OS
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div

object Directories {

	val home = Path(System.getProperty("user.home"))

	val config = when (OS.get()) {
		OS.WINDOWS -> Path(System.getenv("APPDATA"))
		OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
		OS.LINUX -> home / ".config"
	} / "kotatsu-dl"

	val configFile = config / "kotatsu-dl-config.yml"

	fun createDirs() {
		config.createDirectories()
	}

}