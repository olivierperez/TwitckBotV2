import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("kapt") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.1"
}

group = "fr.o80.twitckbot"
version = "2.0"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    // Decompose
    implementation("com.arkivanov.decompose:decompose:0.8.0")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.8.0")

    // Oauth: Ktor + OauthClient + HttpClient
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // Dependency injection
    implementation("com.google.dagger:dagger:2.44")
    kapt("com.google.dagger:dagger-compiler:2.44")

    // IRC
    implementation("pircbot:pircbot:1.5.0")

    // Log
    implementation("ch.qos.logback:logback-classic:1.4.4")

    // LWJGL
    implementation("org.lwjgl:lwjgl:3.2.1")
    implementation("org.lwjgl.osgi:org.lwjgl.glfw:3.2.1.2")
    implementation("org.lwjgl.osgi:org.lwjgl.opengl:3.2.1.2")
    implementation("org.lwjgl.osgi:org.lwjgl.stb:3.2.1.2")

    // Websockets
    implementation("com.github.olivierperez.KotlinSlobs:lib:1.2")

    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("io.ktor:ktor-websockets:1.6.7")

    // Test
    testImplementation("io.mockk:mockk:1.13.2")
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