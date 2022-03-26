package org.koitharu.kotatsu_dl

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.koitharu.kotatsu.parsers.util.toFileNameSafe
import org.koitharu.kotatsu_dl.model.MangaChapterRenderer
import org.koitharu.kotatsu_dl.model.MultiSelectionModel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Frame
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.filechooser.FileNameExtensionFilter

class DownloadDialog(
	owner: Frame?,
	private val coordinator: DownloadsCoordinator,
) : JDialog(owner, true), ListSelectionListener {

	private val messages = ResourceBundle.getBundle("messages")
	private val listChapters = JList<MangaChapter>().apply {
		cellRenderer = MangaChapterRenderer()
		selectionModel = MultiSelectionModel()
		selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
		addListSelectionListener(this@DownloadDialog)
	}
	private val comboBoxBranches = JComboBox<String?>().apply {
		maximumSize = Dimension(maximumSize.width, minimumSize.height)
		alignmentX = LEFT_ALIGNMENT
	}
	private val buttonCancel = JButton(messages.getString("cancel"))
	private val buttonOk = JButton(messages.getString("download"))

	private lateinit var manga: Manga

	init {
		contentPane.layout = BorderLayout()
		buttonCancel.addActionListener { dispose() }
		buttonOk.addActionListener { onOkClick() }
		comboBoxBranches.addActionListener { showChapters() }

		val listScroller = JScrollPane(
			listChapters,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
		)
		listScroller.preferredSize = Dimension(260, 320)
		listScroller.alignmentX = LEFT_ALIGNMENT
		val listPane = JPanel()
		listPane.layout = BoxLayout(listPane, BoxLayout.PAGE_AXIS)
		val label = JLabel(messages.getString("select_chapters_download"))
		label.alignmentX = LEFT_ALIGNMENT
		listPane.add(label)
		listPane.add(Box.createRigidArea(Dimension(0, 5)))
		listPane.add(comboBoxBranches)
		listPane.add(Box.createRigidArea(Dimension(0, 5)))
		listPane.add(listScroller)
		listPane.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
		contentPane.add(listPane, BorderLayout.CENTER)

		val buttonPane = JPanel()
		buttonPane.layout = BoxLayout(buttonPane, BoxLayout.LINE_AXIS)
		buttonPane.border = BorderFactory.createEmptyBorder(0, 10, 10, 10)
		buttonPane.add(Box.createHorizontalGlue())
		buttonPane.add(buttonCancel)
		buttonPane.add(Box.createRigidArea(Dimension(10, 0)))
		buttonPane.add(buttonOk)
		contentPane.add(buttonPane, BorderLayout.PAGE_END)
		pack()
		setLocationRelativeTo(owner)
	}

	override fun valueChanged(event: ListSelectionEvent?) {
		buttonOk.isEnabled = !listChapters.isSelectionEmpty
	}

	fun show(manga: Manga) {
		this.manga = manga
		val branches = manga.chapters?.mapToSet { it.branch }?.toTypedArray().orEmpty()
		comboBoxBranches.model = DefaultComboBoxModel(branches)
		comboBoxBranches.isVisible = branches.size > 1
		showChapters()
		valueChanged(null)
		title = manga.title
		isVisible = true
	}

	private fun showChapters() {
		val selectedBranch = comboBoxBranches.selectedItem
		listChapters.setListData(
			manga.chapters?.filter {
				it.branch == selectedBranch
			}?.toTypedArray().orEmpty()
		)
	}

	private fun onOkClick() {
		val destination = pickDestination() ?: return
		val chaptersIds = listChapters.selectedValuesList.mapToSet { it.id }
		coordinator.addTask(manga, chaptersIds, destination)
		dispose()
	}

	private fun pickDestination(): File? {
		val fileChooser = JFileChooser()
		fileChooser.addChoosableFileFilter(
			FileNameExtensionFilter(messages.getString("comic_zip_archive"), "cbz")
		)
		fileChooser.isAcceptAllFileFilterUsed = false
		fileChooser.selectedFile = File(manga.title.toFileNameSafe() + ".cbz")
		val selection = fileChooser.showSaveDialog(this)
		return if (selection == JFileChooser.APPROVE_OPTION) fileChooser.selectedFile else null
	}
}