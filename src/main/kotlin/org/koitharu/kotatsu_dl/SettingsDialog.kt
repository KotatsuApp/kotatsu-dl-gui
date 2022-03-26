package org.koitharu.kotatsu_dl

import org.koitharu.kotatsu_dl.env.Settings
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Frame
import java.util.*
import javax.swing.*

class SettingsDialog(owner: Frame?) : JDialog(owner) {

	private val messages = ResourceBundle.getBundle("messages")
	private val settings = Settings.getInstance()

	init {
		title = messages.getString("settings")
		with(contentPane) {
			layout = BorderLayout()
			add(createContentPanel(), BorderLayout.CENTER)
			add(createButtonsPanel(), BorderLayout.PAGE_END)
		}
		isModal = true
		isResizable = false
		pack()
		setLocationRelativeTo(owner)
	}

	private fun createButtonsPanel() = JPanel().apply {
		layout = BoxLayout(this, BoxLayout.LINE_AXIS)
		border = BorderFactory.createEmptyBorder(0, 10, 10, 10)
		add(Box.createHorizontalGlue())
		val buttonCancel = JButton(messages.getString("cancel"))
		buttonCancel.addActionListener { dispose() }
		add(buttonCancel)
		add(Box.createRigidArea(Dimension(10, 0)))
		val buttonOk = JButton("OK")
		buttonOk.addActionListener {
			saveSettings()
			dispose()
		}
		add(buttonOk)
	}

	private fun createContentPanel() = JPanel().apply {
		layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
		border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
		val labelStub = JLabel("Not implemented")
		labelStub.preferredSize = Dimension(200, 140)
		add(labelStub)
	}

	private fun saveSettings() {
		settings.flush()
	}
}