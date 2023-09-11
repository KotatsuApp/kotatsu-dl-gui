package org.koitharu.kotatsu_dl.logic

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import java.util.*

class KotatsuState(val sources: List<MangaSource>) {

	val downloading = mutableStateMapOf<Manga, Progress>()
	val isDownloading by derivedStateOf { downloading.isNotEmpty() }
	val failedDownloads = mutableStateSetOf<Manga>()

}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())