package fr.o80.twitckbot.system.event

import fr.o80.twitckbot.system.bean.Viewer

sealed interface Event

class MessageEvent(
    val channel: String,
    val viewer: Viewer,
    val message: String
) : Event

class SendMessageEvent(
    val channel: String,
    val message: String,
    // TODO coolDown: CoolDown,
    // TODO priority: Priority,
) : Event
