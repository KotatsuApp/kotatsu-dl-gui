package org.koitharu.kotatsu_dl.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

@Composable
fun SliderSwitch(
	modifier: Modifier = Modifier,
	valueRange: IntRange = 1..16,
): Float {
	var sliderPosition by remember { mutableStateOf(2f) }
	var sliderPos by remember { mutableStateOf(sliderPosition.toString()) }

	// This is called too often, and should only be called when textfield triggers onValueChange but cant? not sure
	//sliderPosition = sliderPos.toFloatOrNull() ?: sliderPosition

	Slider(
		value = sliderPosition,
		colors = SliderDefaults.colors(
			thumbColor = MaterialTheme.colorScheme.error, Color.Transparent,
			activeTrackColor = MaterialTheme.colorScheme.errorContainer, Color.Transparent
		),
		onValueChangeFinished = {
			sliderPos = sliderPosition.toString()
		},
		valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
		steps = valueRange.last - valueRange.first,
		onValueChange = {
			sliderPosition = it.roundToLong().toFloat()
		},
		modifier = modifier
	)
	return sliderPosition
}