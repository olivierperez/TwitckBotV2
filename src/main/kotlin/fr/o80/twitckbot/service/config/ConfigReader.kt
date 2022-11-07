package fr.o80.twitckbot.service.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

const val CONFIG_DIR = ".config"

val configDeserializer = Json {
    ignoreUnknownKeys = true
}

inline fun <reified T> readConfig(filename: String): T {
    return File(CONFIG_DIR, filename)
        .bufferedReader()
        .use { reader -> configDeserializer.decodeFromString(reader.readText()) }
}
