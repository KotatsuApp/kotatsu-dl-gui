package org.koitharu.kotatsu_dl.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

fun File.subdir(name: String) = File(this, name).also {
	if (!it.exists()) it.mkdirs()
}

fun File.takeIfReadable() = takeIf { it.exists() && it.canRead() }

fun ZipFile.readText(entry: ZipEntry) = getInputStream(entry).bufferedReader().use {
	it.readText()
}

suspend fun File.deleteAwait() = withContext(Dispatchers.IO) {
	delete()
}

suspend fun File.computeSize(): Long = runInterruptible(Dispatchers.IO) {
	computeSizeInternal(this)
}

private fun computeSizeInternal(file: File): Long {
	if (file.isDirectory) {
		val files = file.listFiles() ?: return 0L
		return files.sumOf { computeSizeInternal(it) }
	} else {
		return file.length()
	}
}