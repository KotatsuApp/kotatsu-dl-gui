package org.koitharu.kotatsu_dl.model

import kotlinx.coroutines.CoroutineScope
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu_dl.component.JMultilineLabel
import org.koitharu.kotatsu_dl.env.Constants
import org.koitharu.kotatsu_dl.util.AsyncImage
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Image
import javax.imageio.ImageIO
import javax.swing.*

class MangaCellRenderer(
	private val scope: CoroutineScope,
	private val padding: Int = 6,
) : JPanel(), ListCellRenderer<Manga> {

	private val imageCache = HashMap<String, Image>()
	private val placeholder = ImageIO.read(ClassLoader.getSystemResource("placeholder.png"))

	private val labelTitle = JMultilineLabel()
	private val labelIcon = JLabel()

	init {
		border = BorderFactory.createEmptyBorder(padding, padding, padding, padding)
		layout = BorderLayout(padding, padding)
		add(labelIcon, BorderLayout.CENTER)
		add(labelTitle, BorderLayout.PAGE_END)
	}

	override fun getListCellRendererComponent(
		list: JList<out Manga>,
		manga: Manga,
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

		labelTitle.font = list.font

		val imageWidth = list.fixedCellWidth - padding * 2
		val imageHeight = (imageWidth * Constants.COVER_ASPECT_RATIO).toInt()
		labelIcon.preferredSize = Dimension(imageWidth, imageHeight)
		labelTitle.preferredSize = Dimension(imageWidth, 50)

		val cover = imageCache[manga.coverUrl]
		labelIcon.icon = ImageIcon(cover ?: placeholder)
		if (cover == null) {
			AsyncImage(scope, manga.source, manga.coverUrl)
				.resize(imageWidth, imageHeight)
				.fallback(placeholder)
				.load {
					if (it != null) {
						imageCache[manga.coverUrl] = it
						list.repaintCell(index)
					}
				}
		}
		labelTitle.text = manga.title
		return this
	}

	private fun JList<*>.repaintCell(index: Int) {
		val bounds = getCellBounds(index, index)
		repaint(bounds)
	}

	fun clearImageCache() {
		imageCache.clear()
	}
}