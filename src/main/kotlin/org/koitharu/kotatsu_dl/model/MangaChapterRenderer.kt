package org.koitharu.kotatsu_dl.model

import org.koitharu.kotatsu.parsers.model.MangaChapter
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.ListCellRenderer

class MangaChapterRenderer : JCheckBox(), ListCellRenderer<MangaChapter> {

	override fun getListCellRendererComponent(
		list: JList<out MangaChapter>,
		value: MangaChapter,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean,
	): Component {
		if (isSelected) {
			background = list.selectionBackground
			foreground = list.selectionForeground
		} else {
			background = list.background
			foreground = list.foreground
		}
		font = list.font
		text = value.name
		this.isSelected = isSelected
		return this
	}
}