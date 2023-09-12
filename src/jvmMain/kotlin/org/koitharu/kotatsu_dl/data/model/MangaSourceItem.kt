package org.koitharu.kotatsu_dl.data.model

import org.koitharu.kotatsu.parsers.model.MangaSource

data class MangaSourceItem(
	val source: MangaSource,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is MangaSourceItem && other.source == source
	}
}