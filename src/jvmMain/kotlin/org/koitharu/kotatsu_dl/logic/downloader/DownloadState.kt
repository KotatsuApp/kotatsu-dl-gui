package org.koitharu.kotatsu_dl.logic.downloader

import org.koitharu.kotatsu.parsers.model.Manga
import java.io.File

sealed interface DownloadState {

	val manga: Manga
	val startId: Long

	fun getTitle(): String

	data class Queued(
		override val manga: Manga,
		override val startId: Long,
	) : DownloadState {

		override fun getTitle() = "Queued"
	}

	data class Preparing(
		override val manga: Manga,
		override val startId: Long,
	) : DownloadState {

		override fun getTitle() = "Preparing..."
	}

	data class Progress(
		override val manga: Manga,
		override val startId: Long,
		val totalChapters: Int,
		val currentChapter: Int,
		val totalPages: Int,
		val currentPage: Int,
	) : DownloadState {

		val max: Int = totalChapters * totalPages

		val progress: Int = totalPages * currentChapter + currentPage + 1

		val percent: Float = progress.toFloat() / max

		override fun getTitle() = "In progress"
	}

	data class Done(
		override val manga: Manga,
		override val startId: Long,
		val destination: File,
	) : DownloadState {
		override fun getTitle() = "Done"
	}

	data class Error(
		override val manga: Manga,
		override val startId: Long,
		val error: Throwable,
	) : DownloadState {

		override fun getTitle() = "Error: ${error.localizedMessage}"
	}

	data class Cancelling(
		override val manga: Manga,
		override val startId: Long,
	) : DownloadState {

		override fun getTitle() = "Cancelling..."
	}

	data class Cancelled(
		override val manga: Manga,
		override val startId: Long,
	) : DownloadState {

		override fun getTitle() = "Cancelled"
	}

	data class PostProcessing(
		override val manga: Manga,
		override val startId: Long,
	) : DownloadState {

		override fun getTitle() = "Processing..."
	}
}