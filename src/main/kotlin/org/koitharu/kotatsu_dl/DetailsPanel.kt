package org.koitharu.kotatsu_dl

import kotlinx.coroutines.*
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu_dl.component.JMultilineLabel
import org.koitharu.kotatsu_dl.env.Constants
import org.koitharu.kotatsu_dl.env.CursorController
import org.koitharu.kotatsu_dl.util.*
import java.awt.Component
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.*
import javax.swing.*

class DetailsPanel(
	private val coroutineScope: CoroutineScope,
	private val downloadsCoordinator: DownloadsCoordinator,
) : JPanel() {

	private val messages = ResourceBundle.getBundle("messages")

	private val progressBar = JProgressBar(JProgressBar.HORIZONTAL).apply {
		isIndeterminate = true
		isVisible = false
	}
	private val labelTitle = JMultilineLabel()
	private val labelSubtitle = JMultilineLabel()
	private val labelDescription = JMultilineLabel()
	private val labelCover = JLabel()
	private val buttonDownload = JButton(messages.getString("download"))

	private var loadedData: Manga? = null
	private var job: Job? = null

	init {
		buttonDownload.addActionListener {
			loadedData?.let { data -> DownloadDialog(parentFrame, downloadsCoordinator).show(data) }
		}
		labelTitle.font = labelTitle.font.createBoldVariance()
		labelDescription.contentType = "text/html"
		layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
		isDoubleBuffered = true
		labelCover.alignmentX = Component.CENTER_ALIGNMENT
		add(labelCover)
		add(Box.createRigidArea(Dimension(0, 5)))
		labelTitle.alignmentX = Component.CENTER_ALIGNMENT
		add(labelTitle)
		add(Box.createRigidArea(Dimension(0, 2)))
		labelSubtitle.alignmentX = Component.CENTER_ALIGNMENT
		add(labelSubtitle)
		add(Box.createRigidArea(Dimension(0, 5)))
		buttonDownload.alignmentX = Component.CENTER_ALIGNMENT
		add(buttonDownload)
		add(Box.createRigidArea(Dimension(0, 5)))
		add(progressBar)
		labelDescription.alignmentX = Component.CENTER_ALIGNMENT
		add(labelDescription)
		add(Box.createRigidArea(Dimension(0, 5)))
		progressBar.alignmentX = Component.CENTER_ALIGNMENT

		addComponentListener(ResizeHelper())
		clearData()
	}

	override fun getMinimumSize(): Dimension {
		return Dimension(
			60,
			super.getMinimumSize().height,
		)
	}

	override fun getPreferredSize(): Dimension {
		return Dimension(
			parent.width,
			super.getPreferredSize().height,
		)
	}

	fun setData(manga: Manga?) {
		val prevJob = job
		prevJob?.cancel()
		loadedData = manga
		if (manga == null) {
			clearData()
			job = null
			return
		}
		job = coroutineScope.launch {
			setDataInternal(manga)
			coroutineContext[CursorController]?.withBusy {
				prevJob?.join()
				progressBar.isVisible = true
				runCatchingCancellable {
					withContext(Dispatchers.Default) {
						val parser = ParsersFactory.create(manga.source)
						parser.getDetails(manga)
					}
				}.onSuccess {
					loadedData = it
					setDataInternal(it)
				}
			}
			progressBar.isVisible = false
		}
	}

	override fun scrollRectToVisible(aRect: Rectangle?) = Unit

	private suspend fun CoroutineScope.setDataInternal(manga: Manga) {
		labelTitle.text = manga.title
		labelSubtitle.text = manga.tags.joinToString { it.title }
		labelDescription.text = manga.description?.let { "<html>$it</html>" }
		buttonDownload.isEnabled = !manga.chapters.isNullOrEmpty()
		AsyncImage(this, manga.source, manga.largeCoverUrl ?: manga.coverUrl)
			.resize(labelCover.preferredSize.width, labelCover.preferredSize.height)
			.load {
				labelCover.icon = ImageIcon(it)
			}.join()
	}

	private fun clearData() {
		labelTitle.text = messages.getString("no_manga_selected")
		labelSubtitle.text = null
		labelCover.icon = null
		buttonDownload.isEnabled = false
	}

	private inner class ResizeHelper : ComponentAdapter() {
		override fun componentResized(e: ComponentEvent?) {
			var w = width.coerceAtMost(260)
			var h = height
			val currentAspectRatio = w.toFloat() / h

			if (currentAspectRatio > Constants.COVER_ASPECT_RATIO) {
				w = (h / Constants.COVER_ASPECT_RATIO).toInt()
			} else {
				h = (w * Constants.COVER_ASPECT_RATIO).toInt()
			}
			labelCover.preferredSize = Dimension(w, h)
			revalidate()
		}
	}
}