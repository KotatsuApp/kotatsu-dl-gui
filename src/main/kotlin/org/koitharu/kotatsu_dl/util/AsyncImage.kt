package org.koitharu.kotatsu_dl.util

import kotlinx.coroutines.*
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.env.MangaLoaderContextImpl
import java.awt.Image
import java.util.function.Consumer
import javax.imageio.ImageIO

class AsyncImage(
	private val scope: CoroutineScope,
	source: MangaSource,
	private val url: String,
) {

	private var targetWidth: Int = -1
	private var targetHeight: Int = -1
	private var fallback: Image? = null

	private val webClient = MangaLoaderContextImpl.newWebClient(source)

	fun resize(width: Int, height: Int): AsyncImage {
		targetHeight = height
		targetWidth = width
		return this
	}

	fun fallback(fallbackImage: Image?): AsyncImage {
		fallback = fallbackImage
		return this
	}

	fun load(callback: Consumer<Image?>): Job = scope.launch {
		val image = withContext(Dispatchers.Default) {
			runCatchingCancellable {
				loadImageImpl()
			}.getOrDefault(fallback)?.adjustSize()
		}
		callback.accept(image)
	}

	private suspend fun loadImageImpl(): Image? {
		return webClient.httpGet(url).use {
			ImageIO.read(checkNotNull(it.body).byteStream())
		}
	}

	private fun Image.adjustSize(): Image {
		return if (targetHeight != -1 && targetWidth != -1) {
			getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)
		} else {
			this
		}
	}
}