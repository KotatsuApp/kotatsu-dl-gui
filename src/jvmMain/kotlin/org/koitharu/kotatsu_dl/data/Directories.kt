package org.koitharu.kotatsu_dl.data

import org.koitharu.kotatsu_dl.util.OS
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div

object Directories {

	val home = Path(System.getProperty("user.home"))

	val config = when (OS.current) {
		OS.WINDOWS -> Path(System.getenv("APPDATA"))
		OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
		OS.LINUX -> home / ".config"
		OS.UNKNOWN -> Path(".")
	} / "kotatsu-dl"

	val cache = when (OS.current) {
		OS.WINDOWS -> Path(System.getenv("LOCALAPPDATA")) / "Caches"
		OS.MAC -> Path(System.getProperty("user.home")) / "Library/Caches"
		OS.LINUX -> home / ".cache"
		OS.UNKNOWN -> Path(".")
	} / "kotatsu-dl"

	val configFile = config / "kotatsu-dl-config.yml"

	fun createDirs() {
		config.createDirectories()
	}
}