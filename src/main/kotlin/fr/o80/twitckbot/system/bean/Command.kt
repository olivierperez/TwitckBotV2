package fr.o80.twitckbot.system.bean

data class Command(
    val tag: String,
    val options: List<String> = emptyList()
)
