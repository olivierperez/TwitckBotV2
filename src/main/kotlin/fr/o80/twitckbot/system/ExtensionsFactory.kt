package fr.o80.twitckbot.system

import fr.o80.twitckbot.Extensions
import fr.o80.twitckbot.di.SessionScope
import javax.inject.Inject

@SessionScope
class ExtensionsFactory @Inject constructor(
    private val extensions: Extensions
) {

    fun create(): List<Extension> =
        extensions.list
}
