package org.koitharu.kotatsu_dl.model

import org.koitharu.kotatsu.parsers.model.MangaSource
import java.awt.Component
import java.util.*
import javax.swing.JList
import javax.swing.plaf.basic.BasicComboBoxRenderer

class MangaSourceRenderer : BasicComboBoxRenderer() {

	private val messages = ResourceBundle.getBundle("messages")

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

		return this
	}
}