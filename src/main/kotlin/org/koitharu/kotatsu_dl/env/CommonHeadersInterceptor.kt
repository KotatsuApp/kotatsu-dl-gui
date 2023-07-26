package org.koitharu.kotatsu_dl.env

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.newParser
import org.koitharu.kotatsu.parsers.util.domain
import org.koitharu.kotatsu.parsers.util.mergeWith
import org.koitharu.kotatsu_dl.env.Constants.HEADER_REFERER
import org.koitharu.kotatsu_dl.env.Constants.HEADER_USER_AGENT

class CommonHeadersInterceptor(
	private val userAgent: String,
) : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val source = request.tag(MangaSource::class.java)
		val parser = source?.let { MangaLoaderContextImpl.newParserInstance(it) }
		val sourceHeaders = parser?.headers
		val headersBuilder = request.headers.newBuilder()
		if (sourceHeaders != null) {
			headersBuilder.mergeWith(sourceHeaders, replaceExisting = false)
		}
		if (headersBuilder[HEADER_USER_AGENT] == null) {
			headersBuilder[HEADER_USER_AGENT] = userAgent
		}
		if (headersBuilder[HEADER_REFERER] == null && parser != null) {
			headersBuilder[HEADER_REFERER] = "https://${parser.domain}/"
		}
		val newRequest = request.newBuilder().headers(headersBuilder.build()).build()
		return if (parser is Interceptor) {
			parser.intercept(ProxyChain(chain, newRequest))
		} else {
			return chain.proceed(newRequest)
		}
	}

	private class ProxyChain(
		private val delegate: Interceptor.Chain,
		private val request: Request,
	) : Interceptor.Chain by delegate {

		override fun request(): Request = request
	}
}