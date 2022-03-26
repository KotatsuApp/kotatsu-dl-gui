package org.koitharu.kotatsu_dl.model

import org.koitharu.kotatsu_dl.util.DownloadTask
import javax.swing.AbstractListModel

class DownloadsListModel(
	private val tasks: List<DownloadTask.State>,
) : AbstractListModel<DownloadTask.State>() {

	override fun getSize(): Int {
		return tasks.size
	}

	override fun getElementAt(index: Int): DownloadTask.State {
		return tasks[index]
	}
}