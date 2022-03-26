package org.koitharu.kotatsu_dl.env

import com.koushikdutta.quack.QuackContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.config.ConfigKey
import org.koitharu.kotatsu.parsers.config.MangaSourceConfig
import org.koitharu.kotatsu.parsers.model.MangaSource
import java.util.concurrent.TimeUnit

object MangaLoaderContextImpl : MangaLoaderContext() {

	override val cookieJar = CookieJarImpl()

	public override val httpClient = OkHttpClient.Builder()
		.cookieJar(cookieJar)
		.connectTimeout(20, TimeUnit.SECONDS)
		.readTimeout(60, TimeUnit.SECONDS)
		.writeTimeout(20, TimeUnit.SECONDS)
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
}