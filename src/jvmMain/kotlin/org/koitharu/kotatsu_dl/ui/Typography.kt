package org.koitharu.kotatsu_dl.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

val Roboto = FontFamily(
	Font("font/Roboto-Regular.ttf", FontWeight.Normal),
	Font("font/Roboto-Medium.ttf", FontWeight.Medium),
	Font("font/Roboto-Bold.ttf", FontWeight.Bold),
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