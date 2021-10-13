import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0-alpha4-build398"
}

group = "fr.o80.twitckbot"
version = "2.0"

repositories {
    jcenter()
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    maven("https://jitpack.io")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    // Decompose
    implementation("com.arkivanov.decompose:decompose:0.2.6")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.2.6")

    // Oauth: Ktor + OauthClient + HttpClient
    implementation("io.ktor:ktor-server-core:1.3.0")
    implementation("io.ktor:ktor-server-netty:1.3.0")
    implementation("com.github.mazine:oauth2-client-kotlin:1.0.2")
    implementation("org.glassfish.jersey.media:jersey-media-moxy:2.13")
    implementation("org.apache.httpcomponents:httpclient:4.5.2")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.39.1")
    kapt("com.google.dagger:dagger-compiler:2.39.1")

    // IRC
    implementation("pircbot:pircbot:1.5.0")

    // Log
    implementation("ch.qos.logback:logback-classic:1.0.13")

    // Test
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TwitckBot2"
            packageVersion = "1.0.0"
        }
    }
}