package org.koitharu.kotatsu_dl

import org.koitharu.kotatsu_dl.env.Settings
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class MainWindowGeometryStore : WindowAdapter() {

	override fun windowOpened(e: WindowEvent) {
		val settings = Settings.getInstance()
		val window = e.window
		settings.mainWindowLocation?.let {
			window.location = it
		}
		window.preferredSize = settings.mainWindowSize
		window.size = window.preferredSize
	}

	override fun windowClosing(e: WindowEvent) {
		val settings = Settings.getInstance()
		val window = e.window
		settings.mainWindowLocation = window.location
		settings.mainWindowSize = window.size
		settings.flush()
	}
}