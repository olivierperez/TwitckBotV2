package fr.o80.twitckbot.utils

fun String.sanitizeLogin() = this.removePrefix("@").lowercase()
