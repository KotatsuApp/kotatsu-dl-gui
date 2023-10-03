package org.koitharu.kotatsu_dl.util

import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.logic.MangaLoaderContextImpl
import java.lang.ref.SoftReference
import java.util.*

object ParsersFactory {

	val all: Set<MangaSource> = EnumSet.allOf(MangaSource::class.java).apply {
		remove(MangaSource.LOCAL)
		remove(MangaSource.DUMMY)
	}

	private val cache = EnumMap<MangaSource, SoftReference<MangaParser>>(MangaSource::class.java)

	fun createParser(source: MangaSource): MangaParser {
		cache[source]?.get()?.let { return it }
		val result = MangaLoaderContextImpl.newParserInstance(source)
		cache[source] = SoftReference(result)
		return result
	}
}