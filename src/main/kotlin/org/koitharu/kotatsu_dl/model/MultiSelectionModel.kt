package org.koitharu.kotatsu_dl.model

import javax.swing.DefaultListSelectionModel

class MultiSelectionModel : DefaultListSelectionModel() {

	override fun setSelectionInterval(index0: Int, index1: Int) {
		if (super.isSelectedIndex(index0)) {
			super.removeSelectionInterval(index0, index1)
		} else {
			super.addSelectionInterval(index0, index1)
		}
	}
}