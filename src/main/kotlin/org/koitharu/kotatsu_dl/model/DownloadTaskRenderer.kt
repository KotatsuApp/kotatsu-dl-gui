package org.koitharu.kotatsu_dl.model

import org.koitharu.kotatsu_dl.env.Constants
import org.koitharu.kotatsu_dl.util.DownloadTask
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Image
import java.util.*
import javax.swing.*
import kotlin.math.roundToInt

class DownloadTaskRenderer(
	private val coverSize: Int,
) : JPanel(), ListCellRenderer<DownloadTask.State> {

	private val messages = ResourceBundle.getBundle("messages")
	private val progressBar = JProgressBar(JProgressBar.HORIZONTAL, 0, 100)
	private val labelTitle = JLabel("")
	private val labelStatus = JLabel(messages.getString("loading_"))
	private val labelCover = JLabel()
	private val panel = JPanel()

	init {
		progressBar.isStringPainted = false
		layout = BorderLayout()
		labelCover.preferredSize = Dimension(coverSize, (coverSize * Constants.COVER_ASPECT_RATIO).roundToInt())
		add(labelCover, BorderLayout.WEST)
		panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
		labelTitle.alignmentX = Component.LEFT_ALIGNMENT
		panel.add(labelTitle)
		panel.add(Box.createRigidArea(Dimension(0, 5)))
		panel.add(Box.createVerticalGlue())
		progressBar.alignmentX = Component.LEFT_ALIGNMENT
		panel.add(progressBar)
		panel.add(Box.createRigidArea(Dimension(0, 5)))
		labelStatus.alignmentX = Component.LEFT_ALIGNMENT
		panel.add(labelStatus)
		panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
		add(panel, BorderLayout.CENTER)
		border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
	}

	override fun getListCellRendererComponent(
		list: JList<out DownloadTask.State>,
		value: DownloadTask.State,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean,
	): Component {
		if (isSelected) {
			background = list.selectionBackground
			foreground = list.selectionForeground
			panel.background = list.selectionBackground
			panel.foreground = list.selectionForeground
		} else {
			background = list.background
			foreground = list.foreground
			panel.background = list.background
			panel.foreground = list.foreground
		}

		labelStatus.font = list.font
		bind(value)
		return this
	}

	private fun bind(state: DownloadTask.State) {
		labelCover.icon = state.cover
			?.getScaledInstance(coverSize, (coverSize * Constants.COVER_ASPECT_RATIO).roundToInt(), Image.SCALE_DEFAULT)
			?.let(::ImageIcon)
		labelTitle.text = state.manga.title
		when (state) {
			is DownloadTask.State.Cancelling -> {
				progressBar.isIndeterminate = true
				labelStatus.text = messages.getString("cancelling_")
			}
			is DownloadTask.State.Done -> {
				progressBar.isIndeterminate = false
				progressBar.value = progressBar.maximum
				labelStatus.text = messages.getString("done")
			}
			is DownloadTask.State.Cancelled -> {
				progressBar.isIndeterminate = false
				progressBar.value = progressBar.minimum
				labelStatus.text = messages.getString("cancelled")
			}
			is DownloadTask.State.Error -> {
				progressBar.isIndeterminate = false
				progressBar.value = progressBar.minimum
				labelStatus.text = messages.getString("error_").format(state.error.localizedMessage)
			}
			is DownloadTask.State.PostProcessing -> {
				progressBar.isIndeterminate = true
				labelStatus.text = messages.getString("post_processing_")
			}
			is DownloadTask.State.Preparing -> {
				progressBar.isIndeterminate = true
				labelStatus.text = messages.getString("preparing_")
			}
			is DownloadTask.State.Progress -> {
				progressBar.isIndeterminate = false
				progressBar.maximum = state.max
				progressBar.value = state.progress
				labelStatus.text = messages.getString("downloading_percent")
					.format((state.percent * 100f).roundToInt())
			}
			is DownloadTask.State.Queued -> {
				progressBar.isIndeterminate = true
				labelStatus.text = messages.getString("queued")
			}
		}
	}
}