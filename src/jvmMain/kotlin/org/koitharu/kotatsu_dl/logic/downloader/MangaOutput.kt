package org.koitharu.kotatsu_dl.logic.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runInterruptible
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import java.io.File

class MangaOutput(
	val file: File,
	private val writer: MangaWriter,
	tempDir: File,
) {

	private var index = MangaIndex(null)
	private val directory = File(tempDir, file.nameWithoutExtension)

	fun prepare(manga: Manga) {
		index = MangaIndex(null)
		index.setMangaInfo(manga, append = true)
	}

	suspend fun cleanup() = runInterruptible(Dispatchers.IO + NonCancellable) {
		directory.deleteRecursively()
	}

	suspend fun compress() {
		runInterruptible(Dispatchers.IO) {
			File(directory, INDEX_ENTRY).writeText(index.toString())
		}
		writer.writeToFile(directory, file)
	}

	suspend fun addCover(file: File, ext: String) = runInterruptible(Dispatchers.IO) {
		val name = buildString {
			append(FILENAME_PATTERN.format(0, 0))
			if (ext.isNotEmpty() && ext.length <= 4) {
				append('.')
				append(ext)
			}
		}
		file.copyTo(File(directory, name), overwrite = true)
		index.setCoverEntry(name)
	}

	suspend fun addPage(
		chapter: MangaChapter,
		file: File,
		pageNumber: Int,
		ext: String,
	) = runInterruptible(Dispatchers.IO) {
		val name = buildString {
			append(FILENAME_PATTERN.format(chapter.number, pageNumber))
			if (ext.isNotEmpty() && ext.length <= 4) {
				append('.')
				append(ext)
			}
		}
		file.renameTo(File(directory, name))
		index.addChapter(chapter)
	}

	companion object {

		private const val FILENAME_PATTERN = "%03d%03d"

		const val INDEX_ENTRY = "index.json"
	}
}