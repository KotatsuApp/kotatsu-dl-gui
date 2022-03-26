package org.koitharu.kotatsu_dl.setings

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Frame
import java.util.*
import javax.swing.*
import javax.swing.UIManager.LookAndFeelInfo

class SettingsDialog(owner: Frame?) : JDialog(owner) {

	private val messages = ResourceBundle.getBundle("messages")
	private val settings = Settings.getInstance()
	private val comboBoxTheme = JComboBox<LookAndFeelInfo>().apply {
		val lookAndFeels = UIManager.getInstalledLookAndFeels()
		model = DefaultComboBoxModel(lookAndFeels)
		renderer = LookAndFillInfoRenderer()
		val currentTheme = UIManager.getLookAndFeel().javaClass.canonicalName
		selectedIndex = lookAndFeels.indexOfFirst {
			it.className == currentTheme
		}
		alignmentX = Component.LEFT_ALIGNMENT
	}

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
		add(JLabel(messages.getString("theme")).apply { alignmentX = Component.LEFT_ALIGNMENT })
		add(Box.createRigidArea(Dimension(0, 5)))
		add(comboBoxTheme)
	}

	private fun saveSettings() {
		val newTheme = (comboBoxTheme.selectedItem as LookAndFeelInfo).className
		if (settings.theme != newTheme) {
			settings.theme = newTheme
			UIManager.setLookAndFeel(newTheme)
			JOptionPane.showMessageDialog(
				this,
				messages.getString("restart_required"),
				title,
				JOptionPane.OK_OPTION or JOptionPane.INFORMATION_MESSAGE,
			)
		}
		settings.flush()
	}
}