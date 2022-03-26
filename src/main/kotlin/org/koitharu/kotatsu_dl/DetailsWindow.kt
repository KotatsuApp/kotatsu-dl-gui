package org.koitharu.kotatsu_dl

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu_dl.util.windowScope
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JScrollPane

class DetailsWindow(
	owner: JFrame?,
	downloadsCoordinator: DownloadsCoordinator,
	manga: Manga,
) : JFrame(manga.title) {

	private val panel = DetailsPanel(windowScope, downloadsCoordinator)

	init {
		defaultCloseOperation = DISPOSE_ON_CLOSE
		contentPane.layout = BorderLayout()
		val scrollPane = JScrollPane(
			panel,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
		)
		contentPane.add(scrollPane, BorderLayout.CENTER)
		setLocationRelativeTo(owner)
		size = Dimension(300, 460)
		isResizable = false

		addWindowListener(WindowShowListener(manga))
	}

	private inner class WindowShowListener(
		private val manga: Manga,
	) : WindowAdapter() {

		override fun windowOpened(e: WindowEvent?) {
			panel.setData(manga)
		}
	}
}