package org.koitharu.kotatsu_dl.model

import kotlinx.coroutines.CoroutineScope
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu_dl.util.AsyncFavicon
import org.koitharu.kotatsu_dl.util.getResIcon
import java.awt.Component
import java.util.*
import javax.swing.Icon
import javax.swing.JList
import javax.swing.plaf.basic.BasicComboBoxRenderer

class MangaSourceRenderer(
	private val coroutineScope: CoroutineScope,
) : BasicComboBoxRenderer() {

	private val iconCache = EnumMap<MangaSource, Icon>(MangaSource::class.java)
	private val messages = ResourceBundle.getBundle("messages")
	private val fallbackIcon = getResIcon("web.png")

	override fun getListCellRendererComponent(
		list: JList<*>,
		value: Any?,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean,
	): Component {
		value as MangaSource
		if (isSelected) {
			background = list.selectionBackground
			foreground = list.selectionForeground
		} else {
			background = list.background
			foreground = list.foreground
		}

		font = list.font
		text = if (value == MangaSource.LOCAL) messages.getString("all_sources") else value.title
		icon = iconCache[value].also { ic ->
			if (ic == null) {
				AsyncFavicon(coroutineScope, value)
					.resize(16, 16)
					.fallback(fallbackIcon)
					.load {
						if (it != null) {
							iconCache[value] = it
							list.repaintRow(index)
						}
					}
			}
		} ?: fallbackIcon
		return this
	}

	private fun JList<*>.repaintRow(index: Int) {
		val bounds = getCellBounds(index, index)
		if (bounds != null) {
			repaint(bounds)
		} else {
			repaint()
		}
	}
}