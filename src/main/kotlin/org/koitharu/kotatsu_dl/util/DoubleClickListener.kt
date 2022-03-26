package org.koitharu.kotatsu_dl.util

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JList

class DoubleClickListener<E>(
	private val onDoubleClick: (item: E) -> Unit,
) : MouseAdapter() {

	@Suppress("UNCHECKED_CAST")
	override fun mouseClicked(e: MouseEvent) {
		val list = e.source as? JList<E> ?: return
		if (e.clickCount == 2) {
			list.selectedIndex = list.locationToIndex(e.point)
			onDoubleClick(list.selectedValue)
		}
	}
}