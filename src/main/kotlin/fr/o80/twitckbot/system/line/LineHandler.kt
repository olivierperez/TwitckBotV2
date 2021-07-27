package fr.o80.twitckbot.system.line

internal interface LineInterpreter {
    suspend fun handle(line: String)
}
