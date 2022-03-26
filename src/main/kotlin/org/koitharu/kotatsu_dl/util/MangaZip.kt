package org.koitharu.kotatsu_dl.util

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.util.toFileNameSafe
import java.io.File

class MangaZip(val file: File) {

	private val writableCbz = WritableCbzFile(file)

	private var index = MangaIndex(null)

	suspend fun prepare(manga: Manga) {
		writableCbz.prepare(overwrite = true)
		index = MangaIndex(writableCbz[INDEX_ENTRY].takeIfReadable()?.readText())
		index.setMangaInfo(manga, append = true)
	}

	suspend fun cleanup() {
		writableCbz.cleanup()
	}

	suspend fun compress(): Boolean {
		writableCbz[INDEX_ENTRY].writeText(index.toString())
		return writableCbz.flush()
	}

	suspend fun addCover(file: File, ext: String) {
		val name = buildString {
			append(FILENAME_PATTERN.format(0, 0))
			if (ext.isNotEmpty() && ext.length <= 4) {
				append('.')
				append(ext)
			}
		}
		writableCbz.put(name, file)
		index.setCoverEntry(name)
	}

	suspend fun addPage(chapter: MangaChapter, file: File, pageNumber: Int, ext: String) {
		val name = buildString {
			append(FILENAME_PATTERN.format(chapter.number, pageNumber))
			if (ext.isNotEmpty() && ext.length <= 4) {
				append('.')
				append(ext)
			}
		}
		writableCbz.put(name, file)
		index.addChapter(chapter)
	}

	companion object {

		private const val FILENAME_PATTERN = "%03d%03d"

		const val INDEX_ENTRY = "index.json"

		fun findInDir(root: File, manga: Manga): MangaZip {
			val name = manga.title.toFileNameSafe() + ".cbz"
			val file = File(root, name)
			return MangaZip(file)
		}
	}
}