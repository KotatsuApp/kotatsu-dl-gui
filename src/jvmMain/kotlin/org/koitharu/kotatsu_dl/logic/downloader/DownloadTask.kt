package org.koitharu.kotatsu_dl.logic.downloader

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import okhttp3.Request
import okio.IOException
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.await
import org.koitharu.kotatsu_dl.C
import org.koitharu.kotatsu_dl.data.Directories
import org.koitharu.kotatsu_dl.logic.MangaLoaderContextImpl
import org.koitharu.kotatsu_dl.util.ParsersFactory
import org.koitharu.kotatsu_dl.util.deleteAwait
import java.io.File

class DownloadTask(
	private val manga: Manga,
	private val chaptersIds: Set<Long>?,
	private val writer: MangaWriter,
	private val destination: File,
) {

	private val okHttp = MangaLoaderContextImpl.httpClient
	private val root = Directories.cache.toFile()
	val startId = System.currentTimeMillis()
	private val tempFile = File(root, "$startId.tmp")

	init {
		root.mkdirs()
	}

	operator fun invoke() = flow {
		semaphore.acquire()
		emit(DownloadState.Preparing(manga, startId))
		var output: MangaOutput? = null
		try {
			val repo = ParsersFactory.createParser(manga.source)
			emit(DownloadState.Preparing(manga, startId))
			val data = if (manga.chapters == null) repo.getDetails(manga) else manga
			output = MangaOutput(destination, writer, root)
			output.prepare(data)
			val coverUrl = data.largeCoverUrl ?: data.coverUrl
			downloadFile(coverUrl, data.source).let { file ->
				output.addCover(file, getFileExtensionFromUrl(coverUrl))

			}
			val chapters = if (chaptersIds == null) {
				data.chapters.orEmpty()
			} else {
				data.chapters.orEmpty().filter { x -> x.id in chaptersIds }
			}
			for ((chapterIndex, chapter) in chapters.withIndex()) {
				if (chaptersIds == null || chapter.id in chaptersIds) {
					val pages = repo.getPages(chapter)
					for ((pageIndex, page) in pages.withIndex()) {
						failsafe@ do {
							try {
								val url = repo.getPageUrl(page)
								val file = downloadFile(url, page.source)
								output.addPage(
									chapter,
									file,
									pageIndex,
									getFileExtensionFromUrl(url),
								)
								getFileExtensionFromUrl(url)
							} catch (e: IOException) {
								continue@failsafe
							}
						} while (false)

						emit(
							DownloadState.Progress(
								manga,
								startId,
								totalChapters = chapters.size,
								currentChapter = chapterIndex,
								totalPages = pages.size,
								currentPage = pageIndex,
							),
						)
					}
				}
			}
			emit(DownloadState.PostProcessing(manga, startId))
			output.compress()
			emit(DownloadState.Done(manga, startId, destination))
		} catch (e: CancellationException) {
			emit(DownloadState.Cancelled(manga, startId))
			throw e
		} catch (e: Throwable) {
			e.printStackTrace()
			emit(DownloadState.Error(manga, startId, e))
		} finally {
			withContext(NonCancellable) {
				output?.cleanup()
				tempFile.deleteAwait()
			}
			semaphore.release()
		}
	}.catch { e ->
		emit(DownloadState.Error(manga, startId, e))
	}

	private suspend fun downloadFile(url: String, source: MangaSource): File {
		val request = Request.Builder()
			.url(url)
			.tag(MangaSource::class.java, source)
			.get()
			.build()
		val call = okHttp.newCall(request)
		var attempts = C.MAX_DOWNLOAD_ATTEMPTS
		while (true) {
			try {
				val response = call.clone().await()
				runInterruptible(Dispatchers.IO) {
					tempFile.outputStream().use { out ->
						checkNotNull(response.body).byteStream().copyTo(out)
					}
				}
				return tempFile
			} catch (e: IOException) {
				attempts--
				if (attempts <= 0) {
					throw e
				} else {
					delay(C.DOWNLOAD_ERROR_DELAY)
				}
			}
		}
	}

	private fun getFileExtensionFromUrl(url: String): String {
		return url.substringBeforeLast('?').substringAfterLast('.', "")
	}

	private companion object {

		val semaphore = Semaphore(4)
	}
}