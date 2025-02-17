package fr.o80.twitckbot.system.command

import fr.o80.twitckbot.system.bean.Command
import java.util.*
import javax.inject.Inject

class CommandParser @Inject constructor() {

    private val regex: Regex = "![a-zA-Z0-9_]+.*".toRegex()

    fun parse(message: String): Command? {
        if (!message.matches(regex)) return null

        val split = message.split(" ")
        val tag = split[0].lowercase(Locale.FRENCH)
        return if (split.size == 1) {
            Command(
                tag = tag
            )
        } else {
            Command(
                tag = tag,
                options = split.subList(1, split.size)
            )
        }
    }

}
