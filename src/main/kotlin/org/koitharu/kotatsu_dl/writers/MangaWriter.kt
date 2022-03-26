package org.koitharu.kotatsu_dl.writers

import java.io.File

interface MangaWriter {

	suspend fun writeToFile(dir: File, destination: File)

	companion object {

		operator fun invoke(ext: String): MangaWriter = when (ext) {
			"cbz", "zip" -> CbzWriter()
			else -> throw IllegalArgumentException("No writer for type $ext available")
		}
	}
}