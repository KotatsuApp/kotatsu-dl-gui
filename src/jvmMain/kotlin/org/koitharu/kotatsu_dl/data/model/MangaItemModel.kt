package org.koitharu.kotatsu_dl.data.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource

sealed class MangaItemModel : ListModel {

	abstract val id: Long
	abstract val manga: Manga
	abstract val title: String
	abstract val coverUrl: String

	val source: MangaSource
		get() = manga.source

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is MangaItemModel && other.javaClass == javaClass && id == other.id
	}

	override fun getChangePayload(previousState: ListModel): Any? {
		return when (previousState) {
			!is MangaItemModel -> super.getChangePayload(previousState)
			else -> null
		}
	}
}