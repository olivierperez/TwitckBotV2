package fr.o80.twitckbot.utils

private val digitRegex = "\\d+".toRegex()

fun String?.tryToInt(): Int? {
    return this?.takeIf { it.matches(digitRegex) }?.toInt()
}

fun String?.tryToLong(): Long? {
    return this?.takeIf { it.matches(digitRegex) }?.toLong()
}
