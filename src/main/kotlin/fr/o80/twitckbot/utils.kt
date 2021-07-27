package fr.o80.twitckbot

typealias OnClick = () -> Unit

fun String.addPrefix(prefix: String): String {
    return if (this.startsWith(prefix)) this
    else prefix + this
}
