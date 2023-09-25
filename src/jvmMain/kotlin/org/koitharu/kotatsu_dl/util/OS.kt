package org.koitharu.kotatsu_dl.util

enum class OS {

	WINDOWS, MAC, LINUX, UNKNOWN;

	companion object {

		val current by lazy(LazyThreadSafetyMode.PUBLICATION, ::get)

		private fun get(): OS {
			val os = System.getProperty("os.name").lowercase()
			return when {
				"win" in os -> WINDOWS
				"nix" in os || "nux" in os || "aix" in os -> LINUX
				"mac" in os -> MAC
				else -> UNKNOWN
			}
		}
	}
}