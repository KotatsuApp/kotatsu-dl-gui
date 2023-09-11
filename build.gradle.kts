import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
	id("org.jetbrains.compose")
}

group = "org.koitharu"
version = "1.0.0-c"

repositories {
	google()
	mavenCentral()
	maven("https://jitpack.io")
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
	jvm {
		jvmToolchain(11)
		withJava()
	}
	sourceSets {
		val jvmMain by getting {
			dependencies {
				implementation(kotlin("reflect"))
				implementation(compose.desktop.currentOs)
				@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
				implementation(compose.material3)
				implementation(compose.material)
				implementation(compose.materialIconsExtended)
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
				implementation("com.charleskorn.kaml:kaml:0.53.0")
				implementation("io.ktor:ktor-client-core:1.6.8")
				implementation("io.ktor:ktor-client-cio:1.6.8")
				implementation("org.json:json:20220320")
				implementation("com.github.nv95:kotatsu-parsers:74ffe9418b")
				implementation("com.squareup.okhttp3:okhttp:4.11.0")
				implementation("com.squareup.okio:okio:3.4.0")
				implementation("org.json:json:20230618")
				implementation("io.webfolder:quickjs:1.1.0")
			}
		}
		val jvmTest by getting
	}
}

compose.desktop {
	application {
		mainClass = "MainKt"
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "kotatsu-dl"
			packageVersion = "1.0.0"
		}
	}
}