package org.koitharu.kotatsu_dl.util

import kotlinx.coroutines.*
import okhttp3.Headers
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.newParser
import org.koitharu.kotatsu_dl.env.Constants
import org.koitharu.kotatsu_dl.env.MangaLoaderContextImpl
import java.awt.Image
import java.util.function.Consumer
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

class AsyncFavicon(
	private val scope: CoroutineScope,
	private val source: MangaSource,
) {

	private var targetWidth: Int = -1
	private var targetHeight: Int = -1
	private var fallback: Icon? = null

	fun resize(width: Int, height: Int): AsyncFavicon {
		targetHeight = height
		targetWidth = width
		return this
	}

	fun fallback(fallbackIcon: Icon?): AsyncFavicon {
		fallback = fallbackIcon
		return this
	}

	fun load(callback: Consumer<Icon?>): Job = scope.launch {
		val icon = withContext(Dispatchers.Default) {
			runCatchingCancellable {
				loadImageImpl()
			}.getOrDefault(fallback)
		}
		callback.accept(icon)
	}

	private suspend fun loadImageImpl(): Icon? {
		val parser = source.newParser(MangaLoaderContextImpl)
		val favicons = parser.getFavicons()
		val size = maxOf(targetHeight, targetWidth)
		val favicon = favicons.find(if (size < 0) 999 else size) ?: return null
		val image = MangaLoaderContextImpl.httpGet(
			favicon.url,
			Headers.headersOf(Constants.HEADER_REFERER, "https://${parser.getDomain()}/"),
		).use {
			ImageIO.read(checkNotNull(it.body).byteStream())
		} ?: return null
		return if (targetHeight != -1 && targetWidth != -1) {
			ImageIcon(image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH))
		} else {
			ImageIcon(image)
		}
	}
}