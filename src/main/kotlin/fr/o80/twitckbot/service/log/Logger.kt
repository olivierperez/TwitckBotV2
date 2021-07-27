package fr.o80.twitckbot.service.log

interface Logger {
//    fun command(command: Command, message: String)
    fun trace(message: String)
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}
