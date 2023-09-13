package org.koitharu.kotatsu_dl.ui.screens.main

import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.material3.PaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koitharu.kotatsu_dl.LocalKotatsuState
import org.koitharu.kotatsu_dl.ui.InfoBar
import org.koitharu.kotatsu_dl.ui.screens.Screen
import org.koitharu.kotatsu_dl.ui.screens.screen
import java.util.*

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainScreen() {
	val state = LocalKotatsuState
	Scaffold(
		bottomBar = { InfoBar() },
	) {
		Row {
			NavigationRail {
				Image(
					painter = painterResource("icon_256.png"),
					contentDescription = "Kotatsu Logo",
					contentScale = ContentScale.FillWidth,
					modifier = Modifier.padding(16.dp).height(42.dp).width(42.dp),
				)
				NavigationRailItem(
					icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
					onClick = { screen = Screen.Settings },
					selected = false,
				)
			}
			Box {
				Card(
					elevation = CardDefaults.cardElevation(0.5.dp),
					shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
					modifier = Modifier.fillMaxSize(),
					colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				) {
					Box {
						Column {
							Text(
								"Sources",
								modifier = Modifier.padding(24.dp),
								style = MaterialTheme.typography.displayMedium,
							)
							PaginatedDataTable(
								columns = listOf(
									DataColumn(width = TableColumnWidth.Wrap) {
										Text(
											text = "Icon",
										)
									},
									DataColumn(width = TableColumnWidth.Wrap) {
										Text("Title")
									},
									DataColumn(width = TableColumnWidth.Wrap) {
										Text("Language")
									},
									DataColumn(width = TableColumnWidth.Wrap) {
										Text("Content type")
									},
								),
								state = rememberPaginatedDataTableState(6),
								modifier = Modifier.fillMaxSize(),
							) {
								for (source in state.allMangaSources) {
									row {
										onClick = { screen = Screen.Source }
										cell {
											Surface(
												shape = RoundedCornerShape(4.dp),
												modifier = Modifier.size(42.dp)
											) {
												KamelImage(
													asyncPainterResource("https://androidexample365.com/assets/favicon.png"),
													contentDescription = "Favicon",
													onLoading = { progress -> CircularProgressIndicator(progress) },
													animationSpec = tween(500),
												)
											}
										}
										cell {
											Text(source.title)
										}
										cell {
											Text(
												source.locale?.toFlagEmoji() ?: "Other",
											)
										}
										cell {
											Text(source.contentType.name)
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

fun String.toFlagEmoji(): String {
	// 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
	if (this.length != 2) {
		return this
	}

	val countryCodeCaps =
		this.uppercase(Locale.getDefault()) // upper case is important because we are calculating offset
	val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
	val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

	// 2. It then checks if both characters are alphabet
	if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
		return this
	}

	return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}