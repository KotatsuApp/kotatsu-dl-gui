package org.koitharu.kotatsu_dl.logic

import com.koushikdutta.quack.QuackContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.config.ConfigKey
import org.koitharu.kotatsu.parsers.config.MangaSourceConfig
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.network.OkHttpWebClient
import org.koitharu.kotatsu.parsers.network.WebClient
import java.util.concurrent.TimeUnit

object MangaLoaderContextImpl : MangaLoaderContext() {

	override val cookieJar = CookieJarImpl()

	private val userAgent = "Kotatsu/%s (Android %s; %s; %s %s; %s)".format(
		/*BuildConfig.VERSION_NAME*/ "4.4",
		/*Build.VERSION.RELEASE*/ "r",
		/*Build.MODEL*/ "",
		/*Build.BRAND*/ "",
		/*Build.DEVICE*/ "",
		/*Locale.getDefault().language*/ "en",
	)
	override val httpClient = OkHttpClient.Builder()
		.cookieJar(cookieJar)
		.connectTimeout(20, TimeUnit.SECONDS)
		.readTimeout(60, TimeUnit.SECONDS)
		.writeTimeout(20, TimeUnit.SECONDS)
		.addInterceptor(CommonHeadersInterceptor(userAgent))
		.build()

	override suspend fun evaluateJs(script: String): String? = withContext(Dispatchers.Main) {
		QuackContext.create().use {
			it.evaluate(script)?.toString()
		}
	}

	override fun getConfig(source: MangaSource): MangaSourceConfig {
		return object : MangaSourceConfig {
			override fun <T> get(key: ConfigKey<T>): T = key.defaultValue
		}
	}

	fun newWebClient(source: MangaSource): WebClient = OkHttpWebClient(httpClient, source)
}
