plugins {
    kotlin("jvm") version "2.0.0"
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.dagger)
}

group = "fr.o80.twitckbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}


dependencies {
    implementation(compose.desktop.currentOs)

    implementation(libs.serializationJson)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.ktor)
    implementation(libs.logback)
    implementation(libs.bundles.lwjgl)
    implementation(libs.pircbot)
    implementation(libs.slobs)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
