package org.koitharu.kotatsu_dl.data

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration

object YamlImpl {
	val yaml = Yaml(
		configuration = YamlConfiguration(
			strictMode = false
		)
	)
}