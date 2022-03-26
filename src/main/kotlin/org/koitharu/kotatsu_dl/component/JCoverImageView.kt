package org.koitharu.kotatsu_dl.component

import org.koitharu.kotatsu_dl.env.Constants
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JPanel
import kotlin.math.roundToInt

class JCoverImageView : JPanel(true) {

	var image: Image? = null
		set(value) {
			field = value
			repaint()
		}

	init {
	    addComponentListener(ResizeHelper())
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		image?.let {
			var w = width
			var h = (w * Constants.COVER_ASPECT_RATIO).roundToInt()
			if (h > height) {
				h = height
				w = (h / Constants.COVER_ASPECT_RATIO).roundToInt()
			}
			g.drawImage(it, 0, 0, w, h, null)
		}
	}

	private inner class ResizeHelper : ComponentAdapter() {
		override fun componentResized(e: ComponentEvent?) {
			val container = parent ?: return
			var width: Int = container.width
			var height: Int = container.height
			val currentAspectRatio = width.toFloat() / height

			if (currentAspectRatio > Constants.COVER_ASPECT_RATIO) {
				width = (height / Constants.COVER_ASPECT_RATIO).toInt()
			} else {
				height = (width * Constants.COVER_ASPECT_RATIO).toInt()
			}
			preferredSize = Dimension(width, height)
			container.revalidate()
		}
	}
}