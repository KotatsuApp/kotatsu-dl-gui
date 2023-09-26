package org.koitharu.kotatsu_dl.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import java.util.*

val Roboto = FontFamily(
	Font("font/Roboto-Regular.ttf", FontWeight.Normal),
	Font("font/Roboto-Medium.ttf", FontWeight.Medium),
	Font("font/Roboto-Bold.ttf", FontWeight.Bold),
)

val NotoEmoji = FontFamily(
	Font("font/NotoColorEmoji-Regular.ttf"),
)

val KotatsuTypography: Typography
	get() {
		val material = Typography()
		return Typography(
			displayLarge = material.displayLarge.copy(fontFamily = Roboto),
			displayMedium = material.displayMedium.copy(fontFamily = Roboto),
			displaySmall = material.displaySmall.copy(fontFamily = Roboto),
			headlineLarge = material.headlineLarge.copy(fontFamily = Roboto),
			headlineMedium = material.headlineMedium.copy(fontFamily = Roboto),
			headlineSmall = material.headlineSmall.copy(fontFamily = Roboto),
			titleLarge = material.titleLarge.copy(fontFamily = Roboto),
			titleMedium = material.titleMedium.copy(fontFamily = Roboto),
			titleSmall = material.titleSmall.copy(fontFamily = Roboto),
			bodyLarge = material.bodyLarge.copy(fontFamily = Roboto),
			bodyMedium = material.bodyMedium.copy(fontFamily = Roboto),
			bodySmall = material.bodySmall.copy(fontFamily = Roboto),
			labelLarge = material.labelLarge.copy(fontFamily = Roboto),
			labelMedium = material.labelMedium.copy(fontFamily = Roboto),
			labelSmall = material.labelSmall.copy(fontFamily = Roboto),
		)
	}

val Locale?.flagEmoji: String?
	get() {
		val c = if (this == null) {
			"qq"
		} else {
			country.ifEmpty {
				when (language) {
					"en" -> "gb"
					"uk" -> "ua"
					"ko" -> "kr"
					"be" -> "by"
					"ja" -> "jp"
					"vi" -> "vn"
					"zh" -> "cn"
					else -> language
				}
			}
		}
		// 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
		if (c.length != 2) {
			return null
		}

		val countryCodeCaps = c.uppercase(Locale.ROOT)
		val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
		val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

		// 2. It then checks if both characters are alphabet
		if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
			return null
		}

		return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
	}