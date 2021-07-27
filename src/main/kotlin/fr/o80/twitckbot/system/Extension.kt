package fr.o80.twitckbot.system

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class Extension {

    protected val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    abstract suspend fun init()

}
