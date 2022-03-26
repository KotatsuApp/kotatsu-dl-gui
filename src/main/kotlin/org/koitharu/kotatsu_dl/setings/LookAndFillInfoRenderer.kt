package org.koitharu.kotatsu_dl.setings

import java.awt.Component
import javax.swing.JList
import javax.swing.UIManager.LookAndFeelInfo
import javax.swing.plaf.basic.BasicComboBoxRenderer

class LookAndFillInfoRenderer : BasicComboBoxRenderer() {

	override fun getListCellRendererComponent(
		list: JList<*>,
		value: Any,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean,
	): Component {
		value as LookAndFeelInfo
		if (isSelected) {
			background = list.selectionBackground
			foreground = list.selectionForeground
		} else {
			background = list.background
			foreground = list.foreground
		}
		font = list.font
		text = value.name
		return this
	}
}