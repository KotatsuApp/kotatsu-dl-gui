package org.koitharu.kotatsu_dl.model

import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import javax.swing.AbstractListModel
import javax.swing.JScrollBar

open class PagedListModel<E>(
	private val listener: PaginationListener,
	private val threshold: Int,
) : AbstractListModel<E>(), AdjustmentListener {

	private var hasNextPage: Boolean = false
	private var isLoading: Boolean = true
	private val items = ArrayList<E>()

	override fun getSize(): Int {
		return items.size
	}

	override fun getElementAt(position: Int): E {
		return items[position]
	}

	override fun adjustmentValueChanged(event: AdjustmentEvent) {
		val scrollBar = event.adjustable as? JScrollBar ?: return
		if (scrollBar.model.run { (value + extent) >= scrollBar.maximum - threshold }) {
			onScrolledToEnd()
		}
	}

	fun clearListData() {
		hasNextPage = false
		val prevSize = items.size
		items.clear()
		fireIntervalRemoved(this, 0, prevSize)
		isLoading = false
	}

	fun appendListData(listItems: List<E>) {
		hasNextPage = if (listItems.isEmpty()) {
			false
		} else {
			val prevSize = items.size
			items.addAll(listItems)
			fireIntervalAdded(this, prevSize, items.size)
			true
		}
		isLoading = false
	}

	private fun onScrolledToEnd() {
		if (hasNextPage && !isLoading) {
			isLoading = true
			hasNextPage = listener.onNeedNextPage(items.size)
		}
	}

	fun interface PaginationListener {

		fun onNeedNextPage(offset: Int): Boolean
	}
}