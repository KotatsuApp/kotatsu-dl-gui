package org.koitharu.kotatsu_dl.ui

import androidx.compose.runtime.compositionLocalOf
import java.util.*

class Resources {
	private val bundle = ResourceBundle.getBundle("messages")
	fun string(name: String): String = bundle.getString(name)
}

val LocalResources = compositionLocalOf<Resources> { error("No local versions provided") }