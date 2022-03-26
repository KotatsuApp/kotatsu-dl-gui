package org.koitharu.kotatsu_dl.component

import java.awt.Color
import java.awt.Graphics
import javax.swing.JTextPane

class JMultilineLabel : JTextPane() {

	init {
		isEditable = false
		isFocusable = false
		isOpaque = true
		background = Color(0f, 0f, 0f, 0f)
	}

	override fun paintComponent(g: Graphics) {
		parent?.let {
			val c = g.color
			g.color = it.background
			g.fillRect(0, 0, width, height)
			g.color = c
		} ?: g.clearRect(0, 0, width, height)
		super.paintComponent(g)
	}
}