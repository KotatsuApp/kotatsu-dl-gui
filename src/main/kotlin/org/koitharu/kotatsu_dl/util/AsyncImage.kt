package org.koitharu.kotatsu_dl.util

import kotlinx.coroutines.*
import org.koitharu.kotatsu_dl.env.MangaLoaderContextImpl
import java.awt.Image
import java.util.function.Consumer
import javax.imageio.ImageIO

class AsyncImage(
	private val scope: CoroutineScope,
	private val url: String,
) {

	private var targetWidth: Int = -1
	private var targetHeight: Int = -1

	fun resize(width: Int, height: Int): AsyncImage {
		targetHeight = height
		targetWidth = width
		return this
	}

	fun load(callback: Consumer<Image?>): Job = scope.launch {
		val image = withContext(Dispatchers.IO) {
			loadImageImpl()
		}
		callback.accept(image)
	}

	private suspend fun loadImageImpl(): Image? {
		val image = MangaLoaderContextImpl.httpGet(url).use {
			ImageIO.read(checkNotNull(it.body).byteStream())
		} ?: return null
		return if (targetHeight != -1 && targetWidth != -1) {
			image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)
		} else {
			image
		}
	}
}