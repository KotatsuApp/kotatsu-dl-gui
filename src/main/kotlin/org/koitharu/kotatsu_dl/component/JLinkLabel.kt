package org.koitharu.kotatsu_dl.component

import org.koitharu.kotatsu_dl.util.createUnderlineVariance
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel

class JLinkLabel @JvmOverloads constructor(text: String? = null) : JLabel(text) {

	init {
		super.setFont(font.createUnderlineVariance())
		foreground = Color.BLUE
		cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
	}

	override fun setFont(font: Font?) {
		super.setFont(font?.createUnderlineVariance())
	}

	fun clearActionListeners() {
		mouseListeners.iterator().forEach {
			removeMouseListener(it)
		}
	}

	fun addActionListener(listener: ActionListener) {
		addMouseListener(
			object : MouseAdapter() {
				override fun mouseClicked(e: MouseEvent) {
					super.mouseClicked(e)
					val actionEvent = ActionEvent(e.source, e.id, "")
					listener.actionPerformed(actionEvent)
				}
			},
		)
	}
}