package org.koitharu.kotatsu_dl.model

import org.koitharu.kotatsu.parsers.model.MangaSource
import javax.swing.DefaultComboBoxModel

class MangaSourceComboBoxModel : DefaultComboBoxModel<MangaSource>(MangaSource.values())
