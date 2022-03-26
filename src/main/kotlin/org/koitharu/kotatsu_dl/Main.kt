package org.koitharu.kotatsu_dl

import org.koitharu.kotatsu_dl.util.CliArguments
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main(args: Array<String>) {
	System.setProperty("awt.useSystemAAFontSettings", "lcd")
	System.setProperty("swing.aatext", "true")
	val cliArgs = CliArguments(args)
	if ("no-theme" !in cliArgs) {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
	}
	SwingUtilities.invokeLater {
		MainWindow(cliArgs).isVisible = true
	}
}