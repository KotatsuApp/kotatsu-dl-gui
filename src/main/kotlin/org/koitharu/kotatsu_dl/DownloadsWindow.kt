package org.koitharu.kotatsu_dl

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koitharu.kotatsu_dl.model.DownloadTaskRenderer
import org.koitharu.kotatsu_dl.model.DownloadsListModel
import org.koitharu.kotatsu_dl.util.*
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.util.*
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class DownloadsWindow(
	private val coordinator: DownloadsCoordinator,
) : JFrame(ResourceBundle.getBundle("messages").getString("downloads")) {

	private val messages = ResourceBundle.getBundle("messages")
	private val listTasks = JList<DownloadTask.State>().apply {
		cellRenderer = DownloadTaskRenderer(80)
	}
	private val popupMenu = JPopupMenu()

	init {
		layout = BorderLayout()
		add(listTasks, BorderLayout.CENTER)
		size = Dimension(400, 380)

		popupMenu.add(
			JMenuItem(messages.getString("cancel")).withActionListener(::onCancelClick),
		)
		listTasks.addMouseListener(ListPopupMenuListener(popupMenu))

		listTasks.addMouseListener(DoubleClickListener(::onDoubleClick))

		coordinator.observeAll()
			.onEach {
				val selIndex = listTasks.selectedIndex
				listTasks.model = DownloadsListModel(it)
				listTasks.selectedIndex = selIndex
			}.launchIn(windowScope)
	}

	private fun onCancelClick() {
		val startId = listTasks.selectedValue?.startId ?: return
		coordinator.cancel(startId)
	}

	private fun onDoubleClick(item: DownloadTask.State) {
		if (item is DownloadTask.State.Done) {
			runCatching {
				Desktop.getDesktop().open(item.destination)
			}.onFailure {
				it.printStackTrace()
			}
		}
	}
}