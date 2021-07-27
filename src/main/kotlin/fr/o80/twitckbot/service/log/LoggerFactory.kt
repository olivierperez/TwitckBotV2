package fr.o80.twitckbot.service.log

import kotlin.reflect.KClass

interface LoggerFactory {
    fun getLogger(klass: KClass<*>): Logger
    fun getLogger(name: String): Logger
}
