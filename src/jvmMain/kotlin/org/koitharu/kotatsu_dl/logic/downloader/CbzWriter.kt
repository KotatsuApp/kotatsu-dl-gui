package org.koitharu.kotatsu_dl.logic.downloader

import kotlinx.coroutines.runInterruptible
import org.koitharu.kotatsu_dl.util.deleteAwait
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class CbzWriter : MangaWriter {

	override suspend fun writeToFile(dir: File, destination: File) {
		val tempFile = File(destination.path + ".tmp")
		if (tempFile.exists()) {
			tempFile.deleteAwait()
		}
		try {
			runInterruptible {
				ZipOutputStream(FileOutputStream(tempFile)).use { zip ->
					dir.listFiles()?.forEach {
						zipFile(it, it.name, zip)
					}
					zip.flush()
				}
			}
			tempFile.renameTo(destination)
		} finally {
			if (tempFile.exists()) {
				tempFile.deleteAwait()
			}
		}
	}

	private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
		if (fileToZip.isDirectory) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(ZipEntry(fileName))
			} else {
				zipOut.putNextEntry(ZipEntry("$fileName/"))
			}
			zipOut.closeEntry()
			fileToZip.listFiles()?.forEach { childFile ->
				zipFile(childFile, "$fileName/${childFile.name}", zipOut)
			}
		} else {
			FileInputStream(fileToZip).use { fis ->
				val zipEntry = ZipEntry(fileName)
				zipOut.putNextEntry(zipEntry)
				fis.copyTo(zipOut)
			}
		}
	}
}