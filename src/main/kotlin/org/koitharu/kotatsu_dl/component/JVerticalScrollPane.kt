package org.koitharu.kotatsu_dl.component

import java.awt.Component
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JScrollPane

class JVerticalScrollPane(
	private val view: Component,
) : JScrollPane(view, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER) {

	init {
		addComponentListener(
			object : ComponentAdapter() {
				override fun componentResized(e: ComponentEvent?) {
					adjustSize()
				}
			},
		)
	}

	private fun adjustSize() {
		val h = viewport.extentSize.height
		viewport.extentSize = Dimension(
			width,
			viewport.extentSize.height,
		)
		view.size = Dimension(width, h)
 		view.minimumSize = Dimension(width, h)
		view.maximumSize = Dimension(width, h)
 		view.preferredSize = Dimension(width, h)
	}
}