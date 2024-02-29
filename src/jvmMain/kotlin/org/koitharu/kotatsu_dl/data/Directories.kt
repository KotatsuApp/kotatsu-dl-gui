package org.koitharu.kotatsu_dl.data

import org.koitharu.kotatsu_dl.util.OS
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.createFile
import kotlin.io.path.createDirectories

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
		if (config.exists()) {
			if (!configFile.exists()) {
				configFile.createFile()
			} else {
				println("The config file has been created.")
			}
		} else {
			config.createDirectories()
		}
	}
}