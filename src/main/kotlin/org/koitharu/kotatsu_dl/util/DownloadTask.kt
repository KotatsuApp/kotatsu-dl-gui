package org.koitharu.kotatsu_dl.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import okhttp3.Request
import okio.IOException
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.await
import org.koitharu.kotatsu_dl.env.Constants
import org.koitharu.kotatsu_dl.env.MangaLoaderContextImpl
import org.koitharu.kotatsu_dl.writers.MangaWriter
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt

private const val MAX_DOWNLOAD_ATTEMPTS = 3
private const val DOWNLOAD_ERROR_DELAY = 500L

class DownloadTask(
	private val manga: Manga,
	private val chaptersIds: Set<Long>?,
	private val writer: MangaWriter,
	private val destination: File,
) {

	private val okHttp = MangaLoaderContextImpl.httpClient
	private val coverWidth = 200
	private val coverHeight = (coverWidth * Constants.COVER_ASPECT_RATIO).roundToInt()
	private val root = File(File(System.getProperty("user.home"), ".cache"), "kotatsu-dl")
	val startId = System.currentTimeMillis()
	private val tempFile = File(root, "$startId.tmp")

	init {
		root.mkdirs()
	}

	operator fun invoke() = flow {
		semaphore.acquire()
		emit(State.Preparing(manga, null, startId))
		var cover: Image? = null
		var output: MangaOutput? = null
		val webClient = MangaLoaderContextImpl.newWebClient(manga.source)
		try {
			val repo = ParsersFactory.create(manga.source)
			cover = runCatchingCancellable {
				webClient.httpGet(
					manga.coverUrl,
				).use {
					ImageIO.read(checkNotNull(it.body).byteStream())
				}.getScaledInstance(coverWidth, coverHeight, Image.SCALE_SMOOTH)
			}.getOrNull()
			emit(State.Preparing(manga, cover, startId))
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
							} catch (e: IOException) {
								continue@failsafe
							}
						} while (false)

						emit(
							State.Progress(
								manga, cover, startId,
								totalChapters = chapters.size,
								currentChapter = chapterIndex,
								totalPages = pages.size,
								currentPage = pageIndex,
							),
						)
					}
				}
			}
			emit(State.PostProcessing(manga, cover, startId))
			output.compress()
			emit(State.Done(manga, cover, startId, destination))
		} catch (_: CancellationException) {
			emit(State.Cancelled(manga, cover, startId))
		} catch (e: Throwable) {
			e.printStackTrace()
			emit(State.Error(manga, cover, startId, e))
		} finally {
			withContext(NonCancellable) {
				output?.cleanup()
				tempFile.deleteAwait()
			}
			semaphore.release()
		}
	}.catch { e ->
		emit(State.Error(manga, null, startId, e))
	}

	private suspend fun downloadFile(url: String, source: MangaSource): File {
		val request = Request.Builder()
			.url(url)
			.tag(MangaSource::class.java, source)
			.get()
			.build()
		val call = okHttp.newCall(request)
		var attempts = MAX_DOWNLOAD_ATTEMPTS
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
					delay(DOWNLOAD_ERROR_DELAY)
				}
			}
		}
	}

	private fun getFileExtensionFromUrl(url: String): String {
		return url.substringAfterLast('.', "")
	}

	private companion object {

		val semaphore = Semaphore(4)
	}

	sealed interface State {

		val manga: Manga
		val cover: Image?
		val startId: Long

		data class Queued(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
		) : State

		data class Preparing(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
		) : State

		data class Progress(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
			val totalChapters: Int,
			val currentChapter: Int,
			val totalPages: Int,
			val currentPage: Int,
		) : State {

			val max: Int = totalChapters * totalPages

			val progress: Int = totalPages * currentChapter + currentPage + 1

			val percent: Float = progress.toFloat() / max
		}

		data class Done(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
			val destination: File,
		) : State

		data class Error(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
			val error: Throwable,
		) : State

		data class Cancelling(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
		) : State

		data class Cancelled(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
		) : State

		data class PostProcessing(
			override val manga: Manga,
			override val cover: Image?,
			override val startId: Long,
		) : State
	}
}