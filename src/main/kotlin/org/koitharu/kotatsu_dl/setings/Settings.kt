package org.koitharu.kotatsu_dl.setings

import org.koitharu.kotatsu_dl.MainWindow
import java.awt.Dimension
import java.awt.Point
import java.lang.ref.WeakReference
import java.util.prefs.Preferences
import javax.swing.UIManager

class Settings private constructor() {

	private val prefs = Preferences.userNodeForPackage(MainWindow::class.java)

	var mainWindowSize: Dimension
		get() = Dimension(
			prefs.getInt("main_window.width", 740),
			prefs.getInt("main_window.height", 460),
		)
		set(value) {
			prefs.putInt("main_window.width", value.width)
			prefs.putInt("main_window.height", value.height)
		}

	var mainWindowLocation: Point?
		get() {
			return Point(
				prefs.getInt("main_window.x", -1),
				prefs.getInt("main_window.y", -1),
			).takeUnless { it.x < 0 || it.y < 0 }
		}
		set(value) {
			if (value != null) {
				prefs.putInt("main_window.x", value.x)
				prefs.putInt("main_window.y", value.y)
			} else {
				prefs.remove("main_window.x")
				prefs.remove("main_window.y")
			}
		}

	var theme: String
		get() = prefs.get("theme", UIManager.getSystemLookAndFeelClassName())
		set(value) {
			prefs.put("theme", value)
		}

	fun flush() {
		prefs.flush()
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