package org.koitharu.kotatsu_dl.util

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JList
import javax.swing.JPopupMenu

class ListPopupMenuListener(
	private val menu: JPopupMenu,
) : MouseAdapter() {

	override fun mousePressed(e: MouseEvent) {
		processEvent(e)
	}

	override fun mouseReleased(e: MouseEvent) {
		processEvent(e)
	}

	private fun processEvent(e: MouseEvent) {
		if (e.isPopupTrigger) {
			val list = e.source as? JList<*> ?: return
			list.selectedIndex = list.locationToIndex(e.point)
			menu.show(list, e.x, e.y)
		}
	}
}