package org.koitharu.kotatsu_dl.util

import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.newParser
import org.koitharu.kotatsu_dl.env.MangaLoaderContextImpl
import java.lang.ref.SoftReference
import java.util.*

object ParsersFactory {

	private val cache = EnumMap<MangaSource, SoftReference<MangaParser>>(MangaSource::class.java)

	fun create(source: MangaSource): MangaParser {
		cache[source]?.get()?.let { return it }
		val result = MangaLoaderContextImpl.newParserInstance(source)
		cache[source] = SoftReference(result)
		return result
	}
}