package org.koitharu.kotatsu_dl.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.koitharu.kotatsu_dl.env.CursorController
import java.awt.Component
import java.awt.Font
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.font.TextAttribute
import javax.imageio.ImageIO
import javax.swing.AbstractButton
import javax.swing.Icon
import javax.swing.ImageIcon
import kotlin.coroutines.CoroutineContext

val Component.parentFrame: Frame?
	get() = when (val p = parent) {
		null -> null
		is Frame -> p
		else -> p.parentFrame
	}

fun getResIcon(name: String): ImageIcon {
	return ImageIcon(ImageIO.read(ClassLoader.getSystemResource(name)))
}

fun Font.createBoldVariance(): Font = deriveFont(style or Font.BOLD)

fun Font.createUnderlineVariance(): Font {
	val attrs = attributes.toMutableMap()
	attrs[TextAttribute.UNDERLINE] = TextAttribute.UNDERLINE_ON
	return deriveFont(attributes)
}

val Frame.windowScope: CoroutineScope
	get() {
		windowListeners.forEach {
			if (it is ScopeWindowListener) {
				return it.scope
			}
		}
		val listener = ScopeWindowListener(CursorController(this))
		addWindowFocusListener(listener)
		return listener.scope
	}

private class ScopeWindowListener(
	context: CoroutineContext,
) : WindowAdapter() {

	private val job = SupervisorJob()
	val scope = CoroutineScope(context + job + Dispatchers.Main.immediate)

	override fun windowClosed(e: WindowEvent?) {
		job.cancelChildren()
	}
}

inline fun <B : AbstractButton> B.withActionListener(crossinline listener: () -> Unit): B = apply {
	addActionListener { listener() }
}