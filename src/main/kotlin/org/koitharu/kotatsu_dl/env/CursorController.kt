package org.koitharu.kotatsu_dl.env

import java.awt.Cursor
import java.awt.Frame
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.SwingUtilities
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class CursorController(
	private val frame: Frame,
) : AbstractCoroutineContextElement(Key) {

	private val counter = AtomicInteger(0)

	inline fun <R> withBusy(block: () -> R): R {
		setBusy(true)
		return try {
			block()
		} finally {
			setBusy(false)
		}
	}

	fun setBusy(isBusy: Boolean) {
		if (isBusy) {
			if (counter.getAndIncrement() == 0) {
				applyCursor(isBusy = true)
			}
		} else {
			if (counter.decrementAndGet() == 0) {
				applyCursor(isBusy = false)
			}
		}
	}

	private fun applyCursor(isBusy: Boolean) {
		val runnable = CursorApplyRunnable(isBusy)
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run()
		} else {
			SwingUtilities.invokeAndWait(runnable)
		}
	}

	companion object Key : CoroutineContext.Key<CursorController>

	private inner class CursorApplyRunnable(
		private val isBusy: Boolean,
	) : Runnable {
		override fun run() {
			val cursor = if (isBusy) {
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
			} else {
				Cursor.getDefaultCursor()
			}
			frame.cursor = cursor
		}
	}
}