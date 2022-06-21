package org.koitharu.kotatsu_dl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.newParser
import org.koitharu.kotatsu_dl.env.CursorController
import org.koitharu.kotatsu_dl.env.MangaLoaderContextImpl
import org.koitharu.kotatsu_dl.model.MangaCellRenderer
import org.koitharu.kotatsu_dl.model.MangaSourceComboBoxModel
import org.koitharu.kotatsu_dl.model.MangaSourceRenderer
import org.koitharu.kotatsu_dl.model.PagedListModel
import org.koitharu.kotatsu_dl.setings.SettingsDialog
import org.koitharu.kotatsu_dl.util.CliArguments
import org.koitharu.kotatsu_dl.util.DoubleClickListener
import org.koitharu.kotatsu_dl.util.getResIcon
import org.koitharu.kotatsu_dl.util.windowScope
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.util.*
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class MainWindow(
	private val args: CliArguments,
) : JFrame("kotatsu-dl"), ListSelectionListener, PagedListModel.PaginationListener {

	private val downloadsCoordinator = DownloadsCoordinator(windowScope)
	private val messages = ResourceBundle.getBundle("messages")

	private val mangaCellRenderer = MangaCellRenderer(windowScope)
	private val mangaListModel = PagedListModel<Manga>(this, 80)

	private val textFieldSearch = JTextField().apply {
		columns = 12
		addActionListener { onSourceChanged() }
	}
	private val comboBoxSource = JComboBox(MangaSourceComboBoxModel()).apply {
		renderer = MangaSourceRenderer()
		addActionListener { onSourceChanged() }
	}
	private val searchButton = JButton(getResIcon("search.png")).apply {
		addActionListener { onSourceChanged() }
		toolTipText = messages.getString("do_search")
	}
	private val settingsButton = JButton(getResIcon("setting.png")).apply {
		addActionListener { openSettings() }
		toolTipText = messages.getString("settings")
	}
	private val listManga = JList<Manga>().apply {
		cellRenderer = mangaCellRenderer
		visibleRowCount = -1
		layoutOrientation = JList.HORIZONTAL_WRAP
		layout = GridLayout()
		selectionMode = ListSelectionModel.SINGLE_SELECTION
		fixedCellWidth = 100
		isDoubleBuffered = true
		model = mangaListModel
		addListSelectionListener(this@MainWindow)
		addMouseListener(DoubleClickListener(::onDoubleClick))
	}
	private val statusBar = StatusBar()
	private val detailsPanel = DetailsPanel(windowScope, downloadsCoordinator)

	private val selectedMangaSource: MangaSource
		get() = comboBoxSource.selectedItem as MangaSource

	init {
		defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
		isResizable = true
		minimumSize = Dimension(160, 100)
		setLocationRelativeTo(null)
		iconImage = getResIcon("icon_256.png").image
		addWindowListener(MainWindowGeometryStore())

		val toolbar = JToolBar(JToolBar.HORIZONTAL)
		toolbar.isFloatable = false
		toolbar.add(comboBoxSource)
		toolbar.addSeparator()
		toolbar.add(JLabel(messages.getString("search_")))
		toolbar.add(textFieldSearch)
		toolbar.add(searchButton)
		toolbar.addSeparator()
		toolbar.add(settingsButton)
		contentPane.layout = BorderLayout()
		contentPane.add(toolbar, BorderLayout.PAGE_START)
		contentPane.add(statusBar, BorderLayout.PAGE_END)
		val splitPane = JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			JScrollPane(
				listManga,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
			).apply {
				minimumSize = Dimension(100, 100)
				verticalScrollBar.addAdjustmentListener(mangaListModel)
			},
			JScrollPane(
				detailsPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
			),
		)
		splitPane.resizeWeight = 0.8
		splitPane.isOneTouchExpandable = true
		splitPane.dividerLocation = (size.width * 0.75).toInt()
		splitPane.isContinuousLayout = true
		contentPane.add(splitPane, BorderLayout.CENTER)
		statusBar.addTasksActionListener {
			DownloadsWindow(downloadsCoordinator).isVisible = true
		}
		statusBar.setReady()
		downloadsCoordinator.activeTasksCount()
			.onEach { statusBar.setActiveTasks(it) }
			.launchIn(windowScope)
	}

	override fun valueChanged(event: ListSelectionEvent) {
		detailsPanel.setData(listManga.selectedValue)
	}

	private fun onSourceChanged() {
		loadManga(0)
	}

	private fun openSettings() {
		val dialog = SettingsDialog(this)
		dialog.isVisible = true
	}

	override fun onNeedNextPage(offset: Int): Boolean {
		loadManga(offset)
		return true
	}

	private fun loadManga(offset: Int) {
		windowScope.launch {
			coroutineContext[CursorController]?.withBusy {
				statusBar.setLoading(messages.getString("loading_"))
				if (offset == 0) {
					mangaListModel.clearListData()
				}
				runCatching {
					val query = textFieldSearch.text?.trim()?.takeUnless(String::isEmpty)
					withContext(Dispatchers.Default) {
						when {
							selectedMangaSource != MangaSource.LOCAL -> {
								val parser = selectedMangaSource.newParser(MangaLoaderContextImpl)
								if (query == null) {
									parser.getList(offset, emptySet(), null)
								} else {
									parser.getList(offset, query)
								}
							}
							offset == 0 -> CompositeSearchHelper(
								sources = MangaSource.values().filterNot { it == MangaSource.LOCAL },
								parallelism = 6,
							).doSearch(query)
							else -> emptyList()
						}
					}
				}.onSuccess { list ->
					if (offset == 0) {
						mangaCellRenderer.clearImageCache()
					}
					mangaListModel.appendListData(list)
				}.onFailure {
					JOptionPane.showMessageDialog(
						this@MainWindow,
						it.localizedMessage,
						title,
						JOptionPane.ERROR_MESSAGE or JOptionPane.OK_OPTION,
					)
				}
				statusBar.setReady()
			}
		}
	}

	private fun onDoubleClick(item: Manga) {
		DetailsWindow(this, downloadsCoordinator, item).isVisible = true
	}
}