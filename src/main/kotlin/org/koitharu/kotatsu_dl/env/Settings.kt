package org.koitharu.kotatsu_dl.env

import java.awt.Dimension
import java.awt.Point
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

private const val FILENAME = "kotatsu.conf"

class Settings private constructor() {

	private val prop = Properties()

	var mainWindowSize: Dimension
		get() = Dimension(
			prop.getProperty("main_window.width")?.toIntOrNull() ?: 740,
			prop.getProperty("main_window.height")?.toIntOrNull() ?: 460,
		)
		set(value) {
			prop.setProperty("main_window.width", value.width.toString())
			prop.setProperty("main_window.height", value.height.toString())
		}

	var mainWindowLocation: Point?
		get() {
			return Point(
				prop.getProperty("main_window.x")?.toIntOrNull() ?: return null,
				prop.getProperty("main_window.y")?.toIntOrNull() ?: return null,
			)
		}
		set(value) {
			if (value != null) {
				prop.setProperty("main_window.x", value.x.toString())
				prop.setProperty("main_window.y", value.y.toString())
			} else {
				prop.remove("main_window.x")
				prop.remove("main_window.y")
			}
		}

	init {
		val file = File(FILENAME)
		if (file.exists()) {
			file.inputStream().use { prop.load(it) }
		}
	}

	fun flush() {
		File(FILENAME).outputStream().use {
			prop.store(it, null)
		}
	}

	companion object {

		private var sharedInstance: WeakReference<Settings>? = null

		fun getInstance(): Settings {
			sharedInstance?.get()?.let { return it }
			return synchronized(this) {
				sharedInstance?.get()?.let { return it }
				val instance = Settings()
				sharedInstance = WeakReference(instance)
				instance
			}
		}
	}
}