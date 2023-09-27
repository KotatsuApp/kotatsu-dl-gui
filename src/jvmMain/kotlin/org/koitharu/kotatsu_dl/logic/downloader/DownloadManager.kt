package org.koitharu.kotatsu_dl.logic.downloader

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.util.toFileNameSafe
import java.awt.FileDialog
import java.io.File

class DownloadManager(
	scope: CoroutineScope,
) {

	private val downloaderScope = scope + Dispatchers.Default
	private val jobs = HashMap<Long, Job>()

	val state = MutableStateFlow<List<StateFlow<DownloadState>>>(emptyList())
	val totalProgress: StateFlow<Float> = state.flatMapLatest { flows ->
		combine(flows) { array ->
			var res = 0f
			var count = 0
			for (a in array) {
				res += when (a) {
					is DownloadState.Cancelled -> continue
					is DownloadState.Cancelling -> continue
					is DownloadState.Done -> 1f
					is DownloadState.Error -> continue
					is DownloadState.PostProcessing -> 1f
					is DownloadState.Preparing -> 0f
					is DownloadState.Progress -> a.percent
					is DownloadState.Queued -> 0f
				}
				count++
			}
			if (count == 0) -1f else res / count.toFloat()
		}
	}.stateIn(downloaderScope, SharingStarted.WhileSubscribed(1000), -1f)

	fun startDownload(window: ComposeWindow, manga: Manga, chapters: Set<Long>): Boolean {
		val dialog = FileDialog(window, "Save manga")
		dialog.mode = FileDialog.SAVE
		dialog.isMultipleMode = false
		dialog.file = manga.title.toFileNameSafe() + ".cbz"
		dialog.isVisible = true
		val res = dialog.file
		if (res.isNullOrEmpty()) {
			return false
		}
		startDownload(manga, chapters, File(dialog.directory, res))
		return true
	}

	fun startDownload(manga: Manga, chapters: Set<Long>, destination: File): Long {
		val newTask = DownloadTask(manga, chapters, MangaWriter(destination.extension), destination)
		val job = Job()
		val stateFlow = newTask().stateIn(
			scope = downloaderScope + job,
			started = SharingStarted.Eagerly,
			initialValue = DownloadState.Queued(manga, newTask.startId),
		)
		state.value += stateFlow
		jobs[newTask.startId] = job
		return newTask.startId
	}

	fun cancel(taskId: Long) {
		jobs[taskId]?.cancel()
	}
}

val LocalDownloadManager = compositionLocalOf<DownloadManager> { error("No local versions provided") }