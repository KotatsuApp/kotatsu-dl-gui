package org.koitharu.kotatsu_dl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu_dl.util.DownloadTask
import java.io.File

class DownloadsCoordinator(parentScope: CoroutineScope) {

	private val scope = parentScope + Dispatchers.Default
	private val state = MutableStateFlow(emptyList<StateFlow<DownloadTask.State>>())
	private val jobs = HashMap<Long, Job>()

	fun addTask(
		manga: Manga,
		chaptersIds: Set<Long>?,
		destination: File,
	) {
		val newTask = DownloadTask(manga, chaptersIds, destination)
		val job = Job()
		val stateFlow = newTask().stateIn(
			scope = scope + job,
			started = SharingStarted.Eagerly,
			initialValue = DownloadTask.State.Queued(manga, null, newTask.startId),
		)
		state.update { it + stateFlow }
		jobs[newTask.startId] = job
	}

	fun activeTasksCount(): Flow<Int> = state.flatMapLatest {
		combine(it) { x -> x.count { state -> state.isActive() } }
	}

	fun observeAll(): Flow<List<DownloadTask.State>> = state.flatMapLatest {
		combine(it) { x -> x.toList() }
	}

	fun cancel(startId: Long) {
		jobs[startId]?.cancel()
	}

	private fun DownloadTask.State.isActive(): Boolean {
		return this !is DownloadTask.State.Done && this !is DownloadTask.State.Error
	}
}