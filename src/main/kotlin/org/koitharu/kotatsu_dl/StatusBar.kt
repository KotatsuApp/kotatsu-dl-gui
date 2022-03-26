package org.koitharu.kotatsu_dl

import org.koitharu.kotatsu_dl.component.JLinkLabel
import java.awt.BorderLayout
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import javax.swing.border.BevelBorder

class StatusBar : JPanel(BorderLayout()) {

	private val labelStatus = JLabel("")
	private val labelTasks = JLinkLabel()
	private val progressBar = JProgressBar()
	private val messages = ResourceBundle.getBundle("messages")

	init {
		progressBar.isIndeterminate = true
		labelTasks.horizontalAlignment = SwingConstants.RIGHT
		add(labelStatus, BorderLayout.WEST)
		add(labelTasks, BorderLayout.CENTER)
		add(progressBar, BorderLayout.EAST)
		border = BorderFactory.createBevelBorder(BevelBorder.LOWERED)
	}

	fun setLoading(text: String) {
		labelStatus.text = text
		progressBar.isVisible = true
	}

	fun setReady() {
		labelStatus.text = messages.getString("ready")
		progressBar.isVisible = false
	}

	fun setActiveTasks(count: Int) {
		labelTasks.text = if (count == 0) null else {
			messages.getString("active_downloads").format(count)
		}
	}

	fun addTasksActionListener(listener: ActionListener) {
		labelTasks.addActionListener(listener)
	}
}