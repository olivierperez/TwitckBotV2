package fr.o80.twitckbot.system.line

import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.command.CommandParser
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.WhisperEvent
import javax.inject.Inject

class WhisperLineInterpreter @Inject constructor(
    private val eventBus: EventBus,
    private val commandParser: CommandParser,
) : LineInterpreter {

    private val regex =
        Regex("@([^ ]+) :([^!]+)![^@]+@[^.]+\\.tmi\\.twitch\\.tv WHISPER ([^ ]+) :(.+)$")

    override suspend fun handle(line: String) {
        regex.find(line)?.let { matchResult ->
            val tags = Tags.from(matchResult.groupValues[1])
            val user = matchResult.groupValues[2]
            val destination = matchResult.groupValues[3]
            val message = matchResult.groupValues[4]

            val viewer = Viewer(
                login = user,
                displayName = tags.displayName,
                badges = tags.badges,
                userId = tags.userId,
                color = tags.color
            )
            val command = commandParser.parse(message)

            if (command != null) {
                eventBus.send(
                    CommandEvent(
                        destination,
                        command,
                        bits = null,
                        viewer
                    )
                )
            } else {
                eventBus.send(
                    WhisperEvent(
                        destination = destination,
                        viewer = viewer,
                        message = message
                    )
                )
            }

        }
    }
}
